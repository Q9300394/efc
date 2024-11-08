package eu.darken.sdmse.common.pkgs

import eu.darken.sdmse.common.files.local.LocalPath
import eu.darken.sdmse.common.pkgs.sources.SharedLibraryPathClaw
import io.kotest.matchers.shouldBe
import okio.ByteString.Companion.decodeHex
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import testhelpers.BaseTest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SharedLibraryPathClawTest : BaseTest() {

    private fun create() = SharedLibraryPathClaw()

    @Test
    fun `parse raw parcel for product_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����-   com.google.android.trichromelibrary_484408833         2   /product/app/TrichromeLibrary/TrichromeLibrary.apk  #   com.google.android.trichromelibrary ~�       #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e   #   com.google.android.trichromelibrary ~�          #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.google.android.webview  ~�       #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.android.chrome  ~�    ����    
        """.trimIndent().toByteArray()

        val path = create().clawOutPath("com.google.android.trichromelibrary", raw)
        path shouldBe LocalPath.build("/product/app/TrichromeLibrary/TrichromeLibrary.apk")
    }

    @Test
    fun `parse raw parcel for product_private_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����   com.google.android.gms        %   /product/priv-app/GmsCore/GmsCore.apk      com.google.android.gms  ��������   #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.google.android.gms  =]&    ��������    
        """.trimIndent().toByteArray()

        val path = create().clawOutPath("com.google.android.gms", raw)
        path shouldBe LocalPath.build("/product/priv-app/GmsCore/GmsCore.apk")
    }

    @Test
    fun `parse raw parcel for system_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����   com.google.android.ext.shared         /   /system/app/GoogleExtShared/GoogleExtShared.apk    android.ext.shared  ��������   #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.google.android.ext.shared          ��������    
        """.trimIndent().toByteArray()

        val path = create().clawOutPath("com.google.android.ext.shared", raw)
        path shouldBe LocalPath.build("/system/app/GoogleExtShared/GoogleExtShared.apk")
    }

    @Test
    fun `parse raw parcel for apex_app`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����   com.android.cts.ctsshim       7   /apex/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk &   com.android.cts.ctsshim.shared_library  ��������   #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      com.android.cts.ctsshim        ��������   
        """.trimIndent().toByteArray()

        val path = create().clawOutPath("com.android.cts.ctsshim", raw)
        path shouldBe LocalPath.build("/apex/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk")
    }

    @Test
    fun `parse raw parcel for random`() {
        val raw = """
        ${'$'}������a��n��d��r��o��i��d��.��c��o��n��t��e��n��t��.��p��m��.��S��h��a��r��e��d��L��i��b��r��a��r��y��I��n��f��o��������������������com.android.cts.ctsshim��������������7������/random/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk��&������com.android.cts.ctsshim.shared_library������������������#������a��n��d��r��o��i��d��.��c��o��n��t��e��n��t��.��p��m��.��V��e��r��s��i��o��n��e��d��P��a��c��k��a��g��e������������com.android.cts.ctsshim��������������������������������
        """.trimIndent().toByteArray()

        val path = create().clawOutPath("com.android.cts.ctsshim", raw)
        path shouldBe LocalPath.build("/random/com.android.apex.cts.shim/app/CtsShim/CtsShim.apk")
    }

    @Test
    fun `parse raw parcel for data app - hex encoded`() {
        val raw = """
        24 00 00 00 61 00 6E 00 64 00 72 00 6F 00 69 00 64 00 2E 00 63 00 6F 00 6E 00 74 00 65 00 6E 00 74 00 2E 00 70 00 6D 00 2E 00 53 00 68 00 61 00 72 00 65 00 64 00 4C 00 69 00 62 00 72 00 61 00 72 00 79 00 49 00 6E 00 66 00 6F 00 00 00 00 00 FF FF FF FF 2D 00 00 00 63 6F 6D 2E 67 6F 6F 67 6C 65 2E 61 6E 64 72 6F 69 64 2E 74 72 69 63 68 72 6F 6D 65 6C 69 62 72 61 72 79 5F 35 36 37 32 36 33 36 33 33 00 00 00 01 00 00 00 01 00 00 00 80 00 00 00 2F 64 61 74 61 2F 61 70 70 2F 7E 7E 42 63 4F 46 47 54 53 6B 4B 44 5F 66 33 38 70 75 6E 37 43 53 6F 51 3D 3D 2F 63 6F 6D 2E 67 6F 6F 67 6C 65 2E 61 6E 64 72 6F 69 64 2E 74 72 69 63 68 72 6F 6D 65 6C 69 62 72 61 72 79 5F 35 36 37 32 36 33 36 33 33 2D 64 4F 64 6C 67 6D 5A 2D 74 63 79 65 4C 37 73 51 6E 41 42 63 46 77 3D 3D 2F 54 72 69 63 68 72 6F 6D 65 4C 69 62 72 61 72 79 2E 61 70 6B 00 00 00 00 23 00 00 00 63 6F 6D 2E 67 6F 6F 67 6C 65 2E 61 6E 64 72 6F 69 64 2E 74 72 69 63 68 72 6F 6D 65 6C 69 62 72 61 72 79 00 91 C1 CF 21 00 00 00 00 02 00 00 00 23 00 00 00 61 00 6E 00 64 00 72 00 6F 00 69 00 64 00 2E 00 63 00 6F 00 6E 00 74 00 65 00 6E 00 74 00 2E 00 70 00 6D 00 2E 00 56 00 65 00 72 00 73 00 69 00 6F 00 6E 00 65 00 64 00 50 00 61 00 63 00 6B 00 61 00 67 00 65 00 00 00 23 00 00 00 63 6F 6D 2E 67 6F 6F 67 6C 65 2E 61 6E 64 72 6F 69 64 2E 74 72 69 63 68 72 6F 6D 65 6C 69 62 72 61 72 79 00 91 C1 CF 21 00 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00
        """.trimIndent().replace(" ", "").decodeHex().toByteArray()

        val path = create().clawOutPath("com.google.android.trichromelibrary", raw)
        path shouldBe LocalPath.build("/data/app/~~BcOFGTSkKD_f38pun7CSoQ==/com.google.android.trichromelibrary_567263633-dOdlgmZ-tcyeL7sQnABcFw==/TrichromeLibrary.apk")
    }

    /**
     * https://github.com/d4rken-org/sdmaid-se/issues/539
     * Android 10, Oxygen OS 10.0.1, Oneplus 5
     */
    @Test
    fun `parse raw parcel for issue #539`() {
        val raw = """
        ${'$'}   a n d r o i d . c o n t e n t . p m . S h a r e d L i b r a r y I n f o     ����-   c o m . g o o g l e . a n d r o i d . t r i c h r o m e l i b r a r y _ 5 7 9 0 1 3 8 3 3          Y   / d a t a / a p p / c o m . g o o g l e . a n d r o i d . t r i c h r o m e l i b r a r y _ 5 7 9 0 1 3 8 3 3 - Y M I K 0 G 2 j D 5 h 6 D u l G 7 _ i d j w = = / b a s e . a p k   #   c o m . g o o g l e . a n d r o i d . t r i c h r o m e l i b r a r y   ��"       #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e   #   c o m . g o o g l e . a n d r o i d . t r i c h r o m e l i b r a r y   ��"          #   a n d r o i d . c o n t e n t . p m . V e r s i o n e d P a c k a g e      c o m . g o o g l e . a n d r o i d . w e b v i e w     ��"    ����
        """.trimIndent().toByteArray()

        val path = create().clawOutPath("com.google.android.trichromelibrary", raw)
        path shouldBe LocalPath.build("/data/app/com.google.android.trichromelibrary_579013833-YMIK0G2jD5h6DulG7_idjw==/base.apk")
    }
}

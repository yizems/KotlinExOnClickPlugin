package cn.yzl.kotlin.ex.click.util

import org.jetbrains.kotlin.idea.caches.resolve.resolveImportReference
import org.jetbrains.kotlin.idea.util.ImportInsertHelper
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile

object KtUtils {
    /**
     * 导包
     *
     * @param ktFile
     * @param set
     */
    fun insertImports(ktFile: KtFile, path: String) {
        val importList = ktFile.importDirectives
        for (importDirective in importList) {
            val importPath = importDirective.importPath
            if (importPath != null) {
                val pathStr = importPath.pathStr
                if (pathStr == path) {
                    return
                }
            }
        }
        ImportInsertHelper.getInstance(ktFile.project)
                .importDescriptor(ktFile, ktFile.resolveImportReference(FqName(path)).iterator().next(), false)
    }
}
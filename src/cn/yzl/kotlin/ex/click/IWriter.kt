package cn.yzl.kotlin.ex.click

import cn.yzl.kotlin.ex.click.model.Element
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.jetbrains.kotlin.idea.caches.resolve.resolveImportReference
import org.jetbrains.kotlin.idea.util.ImportInsertHelper
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.util.*

/**
 * Created by YZL on 2017/8/14.
 */
class IWriter(protected var mProject: Project, protected var mFile: PsiFile, protected var mClass: KtClass, private val types: List<Element>,
              protected var mFactory: PsiElementFactory, vararg files: PsiFile) : WriteCommandAction.Simple<Any>(mProject, mFile) {
    protected var ktPsiFactory: KtPsiFactory

    init {
        ktPsiFactory = KtPsiFactory(mClass.project)
    }

    @Throws(Throwable::class)
    override fun run() {
        val styleManager = JavaCodeStyleManager.getInstance(mProject)

        createCode()

        //重新格式化代码
        styleManager.optimizeImports(mFile)
        styleManager.shortenClassReferences(mClass)
        ReformatCodeProcessor(mProject, mClass.containingFile, null, false).runWithoutProgress()
    }

    private fun createCode() {
        run {
            //onclick(v:View)
            val builder = StringBuilder()

            builder.append("override fun onClick(v: View?) {")
            builder.append("\nsuper.onClick(v)")
            builder.append("\n when (v) {")


            for (i in types.indices) {
                val element = types[i]
                builder.append("\n" + element.id + " -> {")
                builder.append("\n}")
            }
            builder.append("\n}")
            builder.append("\n}")
            if (mClass.getBody() != null && mClass.getBody()!!.lastChild != null) {
                try {
                    mClass.getBody()!!.addBefore(ktPsiFactory.createFunction(builder.toString()),
                            mClass.getBody()!!.lastChild)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        run {
            //创建 设置函数
            val builder = StringBuilder()

            builder.append("override fun initViewClickListeners() {")

            for (i in types.indices) {
                val element = types[i]
                builder.append("\n" + element.id + ".setOnClickListener(this)")
            }
            builder.append("\n}")

            if (mClass.getBody() != null && mClass.getBody()!!.lastChild != null) {
                try {
                    mClass.getBody()!!.addBefore(ktPsiFactory.createFunction(builder.toString()),
                            mClass.getBody()!!.lastChild)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 导包
     *
     * @param ktFile
     * @param set
     */
    private fun insertImports(ktFile: KtFile, set: HashSet<String>) {
        // Check if already imported Parcel and Parcelable
        for (path in set) {
            val importList = ktFile.importDirectives
            for (importDirective in importList) {
                val importPath = importDirective.importPath
                if (importPath != null) {
                    val pathStr = importPath.pathStr
                    if (pathStr != path) {
                        ImportInsertHelper.getInstance(ktFile.project)
                                .importDescriptor(ktFile, ktFile.resolveImportReference(FqName(path)).iterator().next(), false)
                    }
                }
            }
        }
    }
}

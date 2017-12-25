package cn.yzl.kotlin.ex.click

import cn.yzl.kotlin.ex.click.model.Element
import cn.yzl.kotlin.ex.click.util.Utils
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.jetbrains.kotlin.idea.caches.resolve.resolveImportReference
import org.jetbrains.kotlin.idea.util.ImportInsertHelper
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Created by YZL on 2017/8/14.
 */
class IWriter(protected var mProject: Project, protected var mFile: PsiFile, protected var mClass: KtClass,
              private val types: List<Element>,
              var layoutName: String?)
    : WriteCommandAction.Simple<Any>(mProject, mFile) {
    var ktPsiFactory: KtPsiFactory

    init {
        ktPsiFactory = KtPsiFactory(mProject, false)
    }

    @Throws(Throwable::class)
    override fun run() {
        val styleManager = JavaCodeStyleManager.getInstance(mProject)

        createCode()
//       重新格式化代码
//        styleManager.optimizeImports(mFile)
//        styleManager.shortenClassReferences(mClass)
        ReformatCodeProcessor(mProject, mFile, null, false).run()
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
        if (!Utils.isEmptyString(layoutName)) {
            insertImports(mClass.containingKtFile, "kotlinx.android.synthetic.main.${layoutName}.${types[0].id}")
            insertImports(mClass.containingKtFile, "android.view.View")
        }
    }

    fun insertImports(ktFile: KtFile, path: String) {
        ktFile.importList
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
        ImportInsertHelper.getInstance(mProject)
                .importDescriptor(ktFile, ktFile.resolveImportReference(FqName(path)).iterator().next(), false)
    }


}

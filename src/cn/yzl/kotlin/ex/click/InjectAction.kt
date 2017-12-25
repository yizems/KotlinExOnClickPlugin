package cn.yzl.kotlin.ex.click

import cn.yzl.kotlin.ex.click.model.Element
import cn.yzl.kotlin.ex.click.util.Utils
import cn.yzl.kotlin.ex.click.view.MyDialog
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.generation.actions.BaseGenerateAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilBase
import org.jetbrains.kotlin.idea.internal.Location
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import java.awt.Toolkit

class InjectAction(handler: CodeInsightActionHandler? = null) : BaseGenerateAction(handler) {

    var laoutName: String? = null;

    override fun isValidForClass(targetClass: PsiClass?): Boolean {
        return true
    }

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return file.name.endsWith(".kt")
    }

    override fun actionPerformed(event: AnActionEvent?) {
        val project = event?.getData(PlatformDataKeys.PROJECT)
        val editor = event?.getData(PlatformDataKeys.EDITOR)
        actionPerformedImpl(project!!, editor)
    }

    override fun actionPerformedImpl(project: Project, editor: Editor?) {
        laoutName = null
        val file = PsiUtilBase.getPsiFileInEditor(editor!!, project)
        var layout = Utils.getLayoutFileFromCaret(editor, file)

        if (layout == null) {
            Utils.showErrorNotification(project, "layout not found")
            return
        }
        laoutName = layout.name.replace(".xml", "")
        var elements = Utils.getIDsFromLayout(layout)
//        var elements=ArrayList<Element>().apply {
//            add(Element("测试1","tvOne"))
//            add(Element("测试2","tvTwo"))
//        }
        if (!elements.isEmpty()) {
            showDialog(project, editor, elements)
        } else {
            Utils.showErrorNotification(project, "No IDs found in layout")
        }
    }

    private fun showDialog(project: Project, editor: Editor, elements: ArrayList<Element>) {
        val file = PsiUtilBase.getPsiFileInEditor(editor, project)
        var ktClass: KtClass? = getPsiClassFromEvent(editor)

        if (ktClass == null || file == null) {
            return
        }

        val dialog = MyDialog(elements, MyDialog.CallBack { result -> createCode(file, ktClass, result) })
        dialog.pack()
        dialog.setSize(500, 500)
        val kit = Toolkit.getDefaultToolkit()    // 定义工具包

        val screenSize = kit.screenSize   // 获取屏幕的尺寸

        val screenWidth = screenSize.width / 2         // 获取屏幕的宽

        val screenHeight = screenSize.height / 2       // 获取屏幕的高

        val height = 500

        val width = 500
        dialog.setLocation(screenWidth - width / 2, screenHeight - height / 2)

        dialog.setVisible(true)
    }

    fun createCode(psiFile: PsiFile, psiClass: KtClass, types: List<Element>) {
        IWriter(psiFile.project, psiFile, psiClass, types, JavaPsiFacade.getElementFactory(psiClass.project),laoutName).execute()
    }

    private fun getPsiClassFromEvent(editor: Editor?): KtClass? {
        //        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor == null) {
            return null
        }
        val project = editor.project ?: return null

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
        if (psiFile == null || psiFile !is KtFile)
            return null

        val location = Location.fromEditor(editor, project)
        val psiElement = psiFile.findElementAt(location.startOffset) ?: return null

        return Utils.getKtClassForElement(psiElement)
    }

}
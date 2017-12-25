package cn.yzl.kotlin.ex.click;

import cn.yzl.kotlin.ex.click.model.Element;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.kotlin.idea.caches.resolve.ResolutionUtils;
import org.jetbrains.kotlin.idea.util.ImportInsertHelper;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.psi.KtImportDirective;
import org.jetbrains.kotlin.psi.KtPsiFactory;
import org.jetbrains.kotlin.resolve.ImportPath;

import java.util.HashSet;
import java.util.List;

/**
 * Created by YZL on 2017/8/14.
 */
public class IWriter extends WriteCommandAction.Simple {

    private List<Element> types;
    protected PsiFile mFile;
    protected Project mProject;
    protected KtClass mClass;
    protected PsiElementFactory mFactory;
    protected KtPsiFactory ktPsiFactory;

    public IWriter(Project project, PsiFile mFile, KtClass mClass, List<Element> types,
                   PsiElementFactory mFactory, PsiFile... files) {
        super(project, files);
        this.mFile = mFile;
        this.mProject = project;
        this.mClass = mClass;
        this.mFactory = mFactory;
        this.types = types;
        ktPsiFactory=new KtPsiFactory(mClass.getProject());
    }

    @Override
    protected void run() throws Throwable {
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);

        createCode();

        //重新格式化代码
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }

    private void createCode() {


        {//onclick(v:View)
            StringBuilder builder = new StringBuilder();

            builder.append("override fun onClick(v: View?) {");
            builder.append("\nsuper.onClick(v)");
            builder.append("\n when (v) {");


            for (int i = 0; i < types.size(); i++) {
                Element element = types.get(i);
                builder.append("\n" + element.id + " -> {");
                builder.append("\n}");
            }
            builder.append("\n}");
            builder.append("\n}");
            if (mClass.getBody() != null && mClass.getBody().getLastChild() != null) {
                try {
                    mClass.getBody().addBefore(ktPsiFactory.createFunction(builder.toString()),
                            mClass.getBody().getLastChild());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        {
            //创建 设置函数
            StringBuilder builder = new StringBuilder();

            builder.append("override fun initViewClickListeners() {");

            for (int i = 0; i < types.size(); i++) {
                Element element = types.get(i);
                builder.append("\n" + element.id + ".setOnClickListener(this)");
            }
            builder.append("\n}");

            if (mClass.getBody() != null && mClass.getBody().getLastChild() != null) {
                try {
                    mClass.getBody().addBefore(ktPsiFactory.createFunction(builder.toString()),
                            mClass.getBody().getLastChild());
                } catch (Exception e) {
                    e.printStackTrace();
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
    private void insertImports(KtFile ktFile, HashSet<String> set) {
        // Check if already imported Parcel and Parcelable
        for (String path : set) {
            List<KtImportDirective> importList = ktFile.getImportDirectives();
            for (KtImportDirective importDirective : importList) {
                ImportPath importPath = importDirective.getImportPath();
                if (importPath != null) {
                    String pathStr = importPath.getPathStr();
                    if (!pathStr.equals(path)) {
                        ImportInsertHelper.getInstance(ktFile.getProject())
                                .importDescriptor(ktFile, ResolutionUtils.resolveImportReference(ktFile, new FqName(path)).iterator().next(), false);
                    }
                }
            }
        }

    }
}

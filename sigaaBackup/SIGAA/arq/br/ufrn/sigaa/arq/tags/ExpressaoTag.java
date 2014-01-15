/**
 *
 */
package br.ufrn.sigaa.arq.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;

/**
 * tag para formatar expressões de componentes curriculares
 * @author Andre Dantas
 *
 */
public class ExpressaoTag extends TagSupport {

	private String expr;

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	@Override
	public int doStartTag() throws JspException {
		ComponenteCurricularDao dao = null;
		String res = "[[ expressão inválida ]]";
		try {
			dao = getDAO(ComponenteCurricularDao.class);
			res = ExpressaoUtil.buildExpressaoFromDBTag(expr, dao);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) dao.close();
		}

		try {
            pageContext.getOut().println(res);
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
	}

}

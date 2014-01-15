package br.ufrn.sigaa.arq.tags;

import org.apache.struts.taglib.TagUtils;

/**
 * Tag re-escrita para suporte à desabilitação de escrita em caso de 
 * criação ou atualização do objeto.
 * Para saber se objeto está para ser criado ou atulizado, testa-se
 * o id do objeto encapsulado da Form.
 * @author Andre M Dantas
 *
 */
public class TextTag extends org.apache.struts.taglib.html.TextTag {

	private String create = "true";

	private String update = "true";

	public String getCreate() {
		return create;
	}

	public void setCreate(String create) {
		this.create = create;
		try {
			if ("false".equalsIgnoreCase(create) &
					isObjTransient()) {
				setReadonly(true);
			} else {
				setReadonly(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;

		try {
			if ("false".equalsIgnoreCase(update) &
					!isObjTransient()) {
				setReadonly(true);
			} else {
				setReadonly(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isObjTransient() throws Exception {
		TagUtils tagUtils = TagUtils.getInstance();
		Integer id = (Integer) tagUtils.lookup(pageContext, name, "obj.id", null);
		if (id <= 0) return true;
		else return false;
	}

}

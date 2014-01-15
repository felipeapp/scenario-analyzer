<%@ taglib uri="/tags/struts-html" prefix="html"  %>

    <ul>
		<li>
			Invenções
			<ul>
				<li><h:commandLink action="#{invencao.iniciar}" value="Notificar Invenção" onclick="setAba('inovacao');"/> </li>
				<li><h:commandLink action="#{invencao.listar}" value="Gerenciar Notificações" onclick="setAba('inovacao');"/> </li>
			</ul>
		</li>
		<li>
			Cadastros
			<ul>
				<li><h:commandLink action="#{tipoInvencao.listar}" value="Gerenciar Tipos de Invenção" onclick="setAba('inovacao');"/> </li>
				<li><h:commandLink action="#{estagioInvencaoBean.listar}" value="Gerenciar Estágios de Desenvolvimento de Invenção" onclick="setAba('inovacao');"/> </li>
				<li> Gerenciar Empresas Júnior </li>
				<li> Gerenciar Incubadoras </li>
			</ul>
		</li>
    </ul>
 
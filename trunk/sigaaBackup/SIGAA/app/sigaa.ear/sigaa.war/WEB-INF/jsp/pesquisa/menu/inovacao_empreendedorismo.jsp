<%@ taglib uri="/tags/struts-html" prefix="html"  %>

    <ul>
		<li>
			Inven��es
			<ul>
				<li><h:commandLink action="#{invencao.iniciar}" value="Notificar Inven��o" onclick="setAba('inovacao');"/> </li>
				<li><h:commandLink action="#{invencao.listar}" value="Gerenciar Notifica��es" onclick="setAba('inovacao');"/> </li>
			</ul>
		</li>
		<li>
			Cadastros
			<ul>
				<li><h:commandLink action="#{tipoInvencao.listar}" value="Gerenciar Tipos de Inven��o" onclick="setAba('inovacao');"/> </li>
				<li><h:commandLink action="#{estagioInvencaoBean.listar}" value="Gerenciar Est�gios de Desenvolvimento de Inven��o" onclick="setAba('inovacao');"/> </li>
				<li> Gerenciar Empresas J�nior </li>
				<li> Gerenciar Incubadoras </li>
			</ul>
		</li>
    </ul>
 
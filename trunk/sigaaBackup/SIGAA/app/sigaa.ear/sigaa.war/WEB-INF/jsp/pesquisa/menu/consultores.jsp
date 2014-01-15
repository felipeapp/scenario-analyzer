<%@ taglib uri="/tags/struts-html" prefix="html"  %>
    <ul>
		<li>Consultores Internos/Externos
			<ul>
			<li><ufrn:link action="pesquisa/cadastroConsultor" param="dispatch=edit" aba="consultores">Cadastrar Consultores</ufrn:link></li>
			<li><ufrn:link action="pesquisa/cadastroConsultor" param="dispatch=list&page=0" aba="consultores">Listar/Alterar Consultores</ufrn:link></li>
			</ul>
		</li>
        <li>Consultoria Especial
            <ul>
                <li><ufrn:link action="/pesquisa/cadastroConsultoriaEspecial" param="dispatch=edit" aba="consultores">Gerenciar Consultoria Especial</ufrn:link></li>
            </ul>
		</li>
		<li> Comissão de Pesquisa
			<ul>
				<li><h:commandLink action="#{membroComissao.preCadastrarMembroComissaoPesquisa}" value="Cadastrar Membro na Comissão de Pesquisa" onclick="setAba('consultores')"/> </li>
				<li><h:commandLink action="#{membroComissao.listarMembroComissaoPesquisa}" value="Alterar/Remover Membro da Comissão de Pesquisa" onclick="setAba('consultores')"/> </li>
			</ul>
		</li>
		<li> Comitê Integrado de Ensino, Pesquisa e Extensão (CIEPE)
			<ul>
				<li><h:commandLink action="#{membroComissao.preCadastrarMembroCIEPE}" value="Cadastrar Membro no CIEPE" onclick="setAba('consultores')" /></li>
				<li><h:commandLink action="#{membroComissao.listarMembroCIEPE}" value="Alterar/Remover Membro do CIEPE" onclick="setAba('consultores')" /></li>
			</ul>
		</li>
<%--
 		<li> Colaboradores Voluntários
			<ul>
				<li><h:commandLink value="Cadastrar" action="#{ colaboradorVoluntario.iniciar }" onclick="setAba('consultores')"/></li>
				<li><h:commandLink value="Remover Colaborador Voluntário" action="#{ colaboradorVoluntario.listar }" onclick="setAba('consultores')"/></li>
			</ul>
		</li>
--%>		
		<li> Bolsistas de Produtividade
			<ul>
				<li><h:commandLink action="#{bolsaObtida.iniciarBolsaProdutividade}" value="Cadastrar" onclick="setAba('consultores')"/> </li>
				<li><h:commandLink action="#{bolsaObtida.filtrarProdutividade}" value="Listar/Alterar" onclick="setAba('consultores')"/> </li>
			</ul>
		</li>
    </ul>

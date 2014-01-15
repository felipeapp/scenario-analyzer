<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%-- MENU DE OP��ES PARA O DOCENTE --%>
<div id="menu-dropdown">
<div class="wrapper">
<h:form>
<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">
	<input type="hidden" name="jscook_action"/>
	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">
		<t:navigationMenuItem itemLabel="Consultar Hist�rico" actionListener="#{ menuDiscente.redirecionar }" itemValue="/ensino/verHistorico.do?dispatch=carregarHistorico"/>
		<t:navigationMenuItem itemLabel="Atestado de Matr�cula" action="#{ portalDiscente.atestadoMatricula }"/>
		<t:navigationMenuItem itemLabel="Matr�cula OnLine" action="#{ matriculaGraduacao.iniciarSolicitacaoMatricula}"/>
		<t:navigationMenuItem itemLabel="Consultar Curso" split="true" actionListener="#{ menuDiscente.redirecionar }" itemValue="/geral/curso/busca_geral.jsf"/>
		<t:navigationMenuItem itemLabel="Consultar Componente Curricular" split="true" actionListener="#{ menuDiscente.redirecionar }" itemValue="/geral/componente_curricular/busca_geral.jsf"/>
		<t:navigationMenuItem itemLabel="Consultar Estrutura Curricular"  actionListener="#{ menuDiscente.redirecionar }" itemValue="/geral/estrutura_curricular/busca_geral.jsf"/>
		<t:navigationMenuItem itemLabel="Consultar Turma" actionListener="#{ menuDiscente.redirecionar }" itemValue="/ensino/criarTurma.do?dispatch=list"/>
		<t:navigationMenuItem itemLabel="Unidades Acad�micas" actionListener="#{ menuDiscente.redirecionar }" itemValue="/geral/unidade/busca_geral.jsf"/>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Pesquisa" id="pesquisa" icon="/img/icones/pesquisa_menu.gif">
		<t:navigationMenuItem itemLabel="Plano de Trabalho">
			<t:navigationMenuItem itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/sigaa/pesquisa/planoTrabalho/wizard.do?dispatch=listar"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Relat�rios de Bolsa">
			<t:navigationMenuItem itemLabel="Relat�rios Parciais">
				<t:navigationMenuItem itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=popularEnvio"/>
				<t:navigationMenuItem itemLabel="Consultar"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem itemLabel="Relat�rios Finais">
				<t:navigationMenuItem itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/cadastroRelatorioDiscenteFinal.do?dispatch=edit"/>
				<t:navigationMenuItem itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/cadastroRelatorioDiscenteFinal.do?dispatch=list"/>
			</t:navigationMenuItem>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Extens�o" id="extensao" icon="/img/icones/extensao_menu.gif">
		<t:navigationMenuItem itemLabel="Plano de Trabalho">
			<t:navigationMenuItem itemLabel="Consultar"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Relat�rios">
			<t:navigationMenuItem itemLabel="Enviar"/>
			<t:navigationMenuItem itemLabel="Consultar"/>
			<t:navigationMenuItem itemLabel="Alterar"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Monitoria" id="monitoria" icon="/img/icones/monitoria_menu.gif">

		<t:navigationMenuItem itemLabel="Atividades">
			<t:navigationMenuItem itemLabel="Cadastrar" actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/AtividadeMonitor/form.jsf"/>
			<t:navigationMenuItem itemLabel="Consultar"  actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/AtividadeMonitor/lista.jsf"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Relat�rio Final">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{relatorioFinalMonitor.listar}"/>
			<t:navigationMenuItem itemLabel="Consultar"  actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/RelatorioFinalMonitor/lista.jsf"/>
		</t:navigationMenuItem>

	</t:navigationMenuItem>

</t:jscookMenu>
</h:form>
</div>
</div>
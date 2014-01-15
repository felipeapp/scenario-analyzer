<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
}

.center {
	text-align: center;
	border-spacing: 3px;
}
-->
</style>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de oferta de
	cursos para Vestibular</h2>
	<h:messages showDetail="true"></h:messages>
	<div class="descricaoOperacao" style="width: 95%;">Selecione uma
	Unidade para exibir a tabela de oferta de vagas de cursos. Digite o
	número de vagas correspondente ao cursos, por período de entrada, e
	clique em atualizar.</div>
	<h:form id="formulario">
		<table class="formulario" width="100%">
			<caption>Informe o ano base e o tipo de entrada</caption>
			<tbody>
				<tr>
					<th width="30%" class="obrigatorio">Ano de entrada:</th>
					<td><h:selectOneMenu value="#{cadastroOfertaVagasCurso.ano}"
						onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}"
						id="ano">
						<f:selectItems value="#{cadastroOfertaVagasCurso.anosCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Forma de Ingresso:</th>
					<td>
						<h:selectOneMenu
							value="#{cadastroOfertaVagasCurso.formaIngresso.id}"
							onchange="submit()"
							valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}"
							id="formaIngresso">
							<f:selectItems value="#{formaIngresso.realizamProcessoSeletivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Unidade Responsável do Curso:</th>
					<td><h:selectOneMenu id="unidade"
						value="#{cadastroOfertaVagasCurso.idUnidade}" onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Matrizes Curriculares:</th>
					<td><h:selectOneMenu id="matriz"
						value="#{cadastroOfertaVagasCurso.apenasMatrizesAtivas}" onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}">
						<f:selectItem itemLabel=" TODAS " />
						<f:selectItems value="#{cadastroOfertaVagasCurso.matrizCurricularCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Modalidade de Ensino:</th>
					<td><h:selectOneMenu id="modalidadeEducacao"
						value="#{cadastroOfertaVagasCurso.modalidadeEducacao}" onchange="submit()"
						valueChangeListener="#{cadastroOfertaVagasCurso.carregaListaOfertaVagasCursoGraduacao}">
						<f:selectItems value="#{cadastroOfertaVagasCurso.modalidadeEducacaoCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center;">
					<c:if test="${cadastroOfertaVagasCurso.cursoDataModel.rowCount > 0}">
						<h:dataTable value="#{cadastroOfertaVagasCurso.cursoDataModel}"
							columnClasses="left,left,left,center,center,center,center"
							rowClasses="linhaPar,linhaImpar" var="item" style="width:100%;">
							<h:column headerClass="left"> 
								<f:facet name="header">
									<h:outputText value="Município" />
								</f:facet>
								<h:outputText value="#{item.curso.municipio.nome}"></h:outputText>
							</h:column>
							<h:column headerClass="left">
								<f:facet name="header">
									<h:outputText value="Curso / Matriz Curricular" />
								</f:facet>
								<h:outputText value="#{item.matrizCurricular}"/>
								<i><h:outputText value="(inativa)" rendered="#{not item.matrizCurricular.ativo}"/></i>
							</h:column>
							<h:column headerClass="left" rendered="#{ cadastroOfertaVagasCurso.EAD }">
								<f:facet name="header">
									<h:outputText value="Pólo" />
								</f:facet>
								<h:outputText value="#{item.polo.cidade.nome}"/>
							</h:column>
							<h:column headerClass="center">
								<f:facet name="header">
									<h:outputText value="1º Período" />
								</f:facet>
								<h:inputText size="3" value="#{item.vagasPeriodo1}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									converter="#{intConverter}"  style="text-align:right;"
									required="true">
								</h:inputText>
								<h:outputText styleClass="required"></h:outputText>
							</h:column>
							<h:column headerClass="center">
								<f:facet name="header">
									<h:outputText value="2º Período" />
								</f:facet>
								<h:inputText size="3" value="#{item.vagasPeriodo2}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									style="text-align:right;"
									converter="#{intConverter}">
								</h:inputText>
								<h:outputText styleClass="required"></h:outputText>
							</h:column>
							<h:column headerClass="center">
								<f:facet name="header">
									<h:outputText value="Ociosas no 1º Período" />
								</f:facet>
								<h:inputText size="3" value="#{item.vagasOciosasPeriodo1}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									converter="#{intConverter}" style="text-align:right;">
								</h:inputText>
								<h:outputText styleClass="required"></h:outputText>
							</h:column>
							<h:column headerClass="center">
								<f:facet name="header">
									<h:outputText value="Ociosas no 2º Período" />
								</f:facet>
								<h:inputText size="3" value="#{item.vagasOciosasPeriodo2}"
									onkeyup="return formatarInteiro(this)" maxlength="3"
									converter="#{intConverter}" style="text-align:right;">
								</h:inputText>
								<h:outputText styleClass="required"></h:outputText>
							</h:column>
						</h:dataTable>
						<table class="formulario" width="100%">
							<tfoot>
								<tr>
									<td colspan="4"><h:commandButton id="cadastrar"
										value="Atualizar" action="#{cadastroOfertaVagasCurso.cadastrar}" />
									<h:commandButton id="cancelar" value="Cancelar"
										onclick="#{confirm}"
										action="#{cadastroOfertaVagasCurso.cancelar}" /></td>
								</tr>
							</tfoot>
						</table>
					</c:if>
					<c:if test="${cadastroOfertaVagasCurso.idUnidade !=0 && cadastroOfertaVagasCurso.cursoDataModel.rowCount ==0}">
						<h:outputText>Não há cursos de graduação cadastrados para esta unidade</h:outputText>
					</c:if>
				</td>
			</tr>
		</tbody>
		</table>
		<br/>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
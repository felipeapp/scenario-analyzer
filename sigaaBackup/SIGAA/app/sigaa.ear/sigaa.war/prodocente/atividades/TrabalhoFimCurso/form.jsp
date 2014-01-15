<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	#form textarea, #form select {
		width: 90%;
	}
</style>

<f:view>
	<c:if test="${!trabalhoFimCurso.preCadastroParaGraduacao && !trabalhoFimCurso.atualizacaoParaGraduacao}">
		<ufrn:subSistema teste="lato">
			<%@include file="/lato/menu_coordenador.jsp" %>
		</ufrn:subSistema>
		<ufrn:subSistema teste="portalDocente">
			<%@include file="/portais/docente/menu_docente.jsp"%>
		</ufrn:subSistema>
	</c:if>

	<h2><ufrn:subSistema /> &gt; Orientação de Trabalho de Conclusão de Curso</h2>

	<c:if test="${(trabalhoFimCurso.preCadastroParaGraduacao and trabalhoFimCurso.obj.id == 0) || trabalhoFimCurso.atualizacaoParaGraduacao}">
		<div class="descricaoOperacao">Caro coordenador,
			<br /><br />
			Agora informe os dados para registrar o trabalho de conclusão de curso na produção intelectual do docente orientador.
		</div>
	</c:if>
	
	<div class="descricaoOperacao">
		O cadastro de orientação de Trabalho de Conclusão de Curso só pode ser realizado para alunos que tenham <b>concluído</b> 
		o curso. <br /> Trabalhos pertencentes a alunos ativos devem ser cadastrados pela coordenação do curso.<br/>
		Somente os trabalhos que posseum anexo serão visualizados no portal público do curso.
	</div>

	<c:if test="${!trabalhoFimCurso.preCadastroParaGraduacao && !trabalhoFimCurso.atualizacaoParaGraduacao}">
		<h:form>
			<div class="infoAltRem" style="width: 100%">
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />
				<h:commandLink value="Listar Orientações de Trabalhos de Fim de Curso Cadastradas" action="#{trabalhoFimCurso.listar }" />
			</div>
	    </h:form>
    </c:if>

   	<c:if test="${trabalhoFimCurso.preCadastroParaGraduacao || trabalhoFimCurso.atualizacaoParaGraduacao}">
		<table class="visualizacao" style="width: 100%">
			<tr>
				<td width="40%" align="right"><b>Orientador:</b></td>
				<c:if test="${not empty trabalhoFimCurso.obj.servidor}">
					<td style="font-weight: normal; text-align: left;">${trabalhoFimCurso.obj.servidor.nome }</td>
				</c:if>
				<c:if test="${not empty trabalhoFimCurso.obj.docenteExterno}">
					<td>${trabalhoFimCurso.obj.docenteExterno.nome }</td>
				</c:if>
			</tr>
			<tr>
				<td align="right"><b>Orientando:</b></td>
				<td style="font-weight: normal; text-align: left;">${trabalhoFimCurso.obj.orientando.nome }</td>
			</tr>
		</table>
	</c:if>
<br />
	<h:form id="form">
		<h:inputHidden value="#{trabalhoFimCurso.confirmButton}" />
		<h:inputHidden value="#{trabalhoFimCurso.obj.id}" />

		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Orientação de Trabalho de Conclusão de Curso</caption>

			<tr>
				<th class="required" width="21%">Ano de Referência:</th>
				<td><h:inputText value="#{trabalhoFimCurso.obj.ano}" size="5" maxlength="4" 
							readonly="#{trabalhoFimCurso.readOnly}"	id="anoReferencia" onkeyup="formatarInteiro(this)" />
				</td>
			</tr>
			<tr>
				<th class="required">Tipo de Trabalho de Conclusão:</th>
				<td><h:selectOneMenu style="width: 70%;" value="#{trabalhoFimCurso.obj.tipoTrabalhoConclusao.id}"
							disabled="#{trabalhoFimCurso.readOnly}" disabledClass="#{trabalhoFimCurso.disableClass}" 
							id="tipoTrabalhoConclusao">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{tipoTrabalhoConclusao.combo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Tipo de Orientação:</th>
				<td><h:selectOneMenu style="width: 70%;" value="#{trabalhoFimCurso.obj.orientacao}" id="tipoOrientacao"
							disabled="#{trabalhoFimCurso.readOnly}"	disabledClass="#{trabalhoFimCurso.disableClass}" >
						<f:selectItem itemValue="0" itemLabel="--- SELECIONE ---" />
						<f:selectItem itemValue="O" itemLabel="ORIENTADOR" />
						<f:selectItem itemValue="C" itemLabel="CO-ORIENTADOR" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Título:</th>
				<td><h:inputTextarea value="#{trabalhoFimCurso.obj.titulo}"	rows="2" 
							readonly="#{trabalhoFimCurso.readOnly}" id="titulo" /></td>
			</tr>
			<c:if test="${!trabalhoFimCurso.preCadastroParaGraduacao && !trabalhoFimCurso.atualizacaoParaGraduacao}">
				<tr>
					<th>Aluno Externo:</th>
					<td><h:selectBooleanCheckbox value="#{trabalhoFimCurso.obj.discenteExterno}" id="discenteExterno"
								readonly="#{trabalhoFimCurso.readOnly}" onchange="submit()" />
						<label for="form:discenteExterno" style="color: #555;"> (selecione esta opção caso o 
							orientando não seja um discente da ${ configSistema['siglaInstituicao'] })</label>
					</td>
				</tr>
				<tr>
					<th class="required">Orientando:</th>
					<td>
						<c:if test="${ not trabalhoFimCurso.obj.discenteExterno}">
							<span id="busca">
			 					<h:inputHidden id="idDiscente" value="#{ trabalhoFimCurso.obj.orientando.id }" />
	 							<h:inputText id="nomeDiscente" value="#{trabalhoFimCurso.obj.orientando.pessoa.nome}" size="80" />
								<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente" 
										baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
										indicator="indicatorDiscente" minimumCharacters="3" 
										parameters="nivel=GL,status=todos,concluido=sim" parser="new ResponseXmlToHtmlListParser()" />
								<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif" /></span>
							</span>
						</c:if>
						 <c:if test="${trabalhoFimCurso.obj.discenteExterno}">
			 				<h:inputText id="orientandoString" maxlength="80" readonly="#{trabalhoFimCurso.readOnly}"
			 						value="#{trabalhoFimCurso.obj.orientandoString}" size="80" />
						 </c:if>
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required">Área de Conhecimento:</th>
				<td><h:selectOneMenu value="#{trabalhoFimCurso.obj.area.id}" disabled="#{trabalhoFimCurso.readOnly}" id="areaConhecimento"
							disabledClass="#{trabalhoFimCurso.disableClass}" valueChangeListener="#{trabalhoFimCurso.changeArea}">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{area.allCombo}" />
						<a4j:support event="onchange" reRender="subarea" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>
				<td><h:selectOneMenu value="#{trabalhoFimCurso.obj.subArea.id}"	disabled="#{trabalhoFimCurso.readOnly}"
							disabledClass="#{trabalhoFimCurso.disableClass}" id="subarea">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{trabalhoFimCurso.subArea}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${!trabalhoFimCurso.preCadastroParaGraduacao && !trabalhoFimCurso.atualizacaoParaGraduacao}">
				<tr>
					<th class="required">Instituição:</th>
					<td><h:selectOneMenu value="#{trabalhoFimCurso.obj.ies.id}" disabled="#{trabalhoFimCurso.readOnly}"
								disabledClass="#{trabalhoFimCurso.disableClass}" id="instituicao">
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
							<f:selectItems value="#{instituicoesEnsino.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Agência Financiadora:</th>
				<td><h:selectOneMenu value="#{trabalhoFimCurso.obj.entidadeFinanciadora.id}" id="entidadeFinanciadora"
							disabled="#{trabalhoFimCurso.readOnly}"	disabledClass="#{trabalhoFimCurso.disableClass}">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar value="#{trabalhoFimCurso.obj.dataInicio}"	size="10" maxlength="10" 
							readonly="#{trabalhoFimCurso.readOnly}" popupDateFormat="dd/MM/yyyy"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required">Data da Defesa:</th>
				<td><t:inputCalendar value="#{trabalhoFimCurso.obj.dataDefesa}"	size="10" maxlength="10" 
							readonly="#{trabalhoFimCurso.readOnly}" popupDateFormat="dd/MM/yyyy"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataDefesa">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th>Informações complementares:</th>
				<td><h:inputTextarea value="#{trabalhoFimCurso.obj.informacao}" rows="4" 
							readonly="#{trabalhoFimCurso.readOnly}" id="informacao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{trabalhoFimCurso.confirmButton}" action="#{trabalhoFimCurso.cadastrar}" id="cadastrar" />
						
						<h:commandButton id="btConfirmarVoltar"	value="<< Dados do Registro da Atividade"
								action="#{trabalhoFimCurso.voltarDadosGraduacao}" 
								rendered="#{(trabalhoFimCurso.preCadastroParaGraduacao and trabalhoFimCurso.obj.id == 0) || trabalhoFimCurso.atualizacaoParaGraduacao}" />
								
						<h:commandButton value="Cancelar" action="#{trabalhoFimCurso.cancelar}" onclick="#{confirm}" id="cancelar" />								
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

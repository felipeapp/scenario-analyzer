<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<c:if test="${!estagio.preCadastroParaGraduacao && !estagio.atualizacaoParaGraduacao}">
<%@include file="/portais/docente/menu_docente.jsp"%>
</c:if>
	<h2><ufrn:subSistema /> > Estágio</h2>

	<c:if test="${estagio.preCadastroParaGraduacao || estagio.atualizacaoParaGraduacao}">
	<div class="descricaoOperacao">
	Caro ${acesso.docente ? 'docente' : 'coordenador'},
	<br><br>
	Agora informe os dados para registrar o estágio ${acesso.docente ? 'do discente orientando.' : 'na produção intelectual do docente orientador.'}
	</div>
	</c:if>

	<c:if test="${!estagio.preCadastroParaGraduacao && !estagio.atualizacaoParaGraduacao}">
	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		<h:commandLink action="#{estagio.listar}" value="Listar Estagios Cadastradas"/>
	 </div>
    </h:form>
	</c:if>
	<c:if test="${estagio.preCadastroParaGraduacao || estagio.atualizacaoParaGraduacao}">

	<table align="center" class="visualizacao" style="width: 100%">
		<tbody>
			<tr>
				<th align="right" width="40%">Orientador:</th>
				<td align="left">${estagio.obj.servidor.nome }</td>
			</tr>
			<tr>
				<th align="right">Orientando:</th>
				<td align="left">${estagio.obj.aluno.nome }</td>
			</tr>
		</tbody>
	</table>

	</c:if>
	<h:messages showDetail="true"></h:messages>


	<h:form id="form">
	<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Orientação de Estágio</caption>
			<h:inputHidden value="#{estagio.confirmButton}" />

			<c:if test="${!estagio.preCadastroParaGraduacao && !estagio.atualizacaoParaGraduacao}">
			<tr>
				<th>Aluno Externo:</th>
				<td>
					 <h:selectBooleanCheckbox
							value="#{estagio.obj.discenteExterno}" id="discenteExterno"
							readonly="#{estagio.readOnly}" onchange="submit()" />
					 <ufrn:help img="/img/prodocente/help.png">Discente cuja graduação não foi cursada na ${ configSistema['siglaInstituicao'] }</ufrn:help>
				</td>
			</tr>
			</c:if>
			<c:if test="${!estagio.preCadastroParaGraduacao && !estagio.atualizacaoParaGraduacao}">
			<tr>
				<th class="required">Orientando:</th>
					<td>
					<c:if test="${ not estagio.obj.discenteExterno}">
						<h:inputHidden id="idDiscente" value="#{ estagio.obj.aluno.id }" rendered="#{!estagio.preCadastroParaGraduacao && !estagio.atualizacaoParaGraduacao}"/>
			 			<h:inputText id="nomeDiscente"
						value="#{estagio.obj.aluno.pessoa.nome}" size="70"  styleClass="textcomplete"/>

						<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G,status=todos"
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDiscente"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
					</c:if>
					<c:if test="${estagio.obj.discenteExterno}">
						<h:inputText id="orientando" maxlength="40"
						value="#{estagio.obj.orientando}" size="80" readonly="#{estagio.readOnly}" />
					</c:if>
				</td>
			</tr>
			</c:if>
			<tr>
				<th class="required">Nome do Projeto:</th>
				<td><h:inputText value="#{estagio.obj.nomeProjeto}" size="80"
					maxlength="255" readonly="#{estagio.readOnly}" id="nomeProjeto" /></td>
			</tr>
			<tr>
				<th class="required">Instituição:</th>
				<td><h:inputText value="#{estagio.obj.instituicao}" size="80"
					maxlength="255" readonly="#{estagio.readOnly}" id="instituicao" /></td>
			</tr>
			<tr>
				<th class="required">Período:</th>
				<td>
					<t:inputCalendar value="#{estagio.obj.periodoInicio}" onkeypress="return(formataData(this, event))"
					size="10" maxlength="10" readonly="#{estagio.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoInicio"/>
					a
					<t:inputCalendar value="#{estagio.obj.periodoFim}" onkeypress="return(formataData(this, event))"
					size="10" maxlength="10" readonly="#{estagio.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="periodoFim"/>
				</td>
			</tr>
			<tr>
				<a4j:region>
				<th class="required">Área de Conhecimento:</th>
				<td><h:selectOneMenu value="#{estagio.obj.area.id}" style="width: 380px"
					disabled="#{estagio.readOnly}" disabledClass="#{estagio.disableClass}"
					id="area" valueChangeListener="#{estagio.changeArea}">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{area.allCombo}" />
					<a4j:support event="onchange" reRender="subArea"/>
				</h:selectOneMenu></td>
				</a4j:region>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>

				<td><h:selectOneMenu value="#{estagio.obj.subArea.id}" style="width: 380px"
					disabled="#{estagio.readOnly}" disabledClass="#{estagio.disableClass}"
					id="subArea">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{estagio.subArea}"/>
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Entidade Financiadora:</th>

				<td><h:selectOneMenu
					value="#{estagio.obj.entidadeFinanciadora.id}" style="width: 380px"
					disabled="#{estagio.readOnly}"
					disabledClass="#{estagio.disableClass}" id="entidadeFinanciadora">
					<f:selectItem itemValue="0" itemLabel="SELECIONE" />
					<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Informações Complementares:</th>
				<td>
					<h:inputTextarea value="#{estagio.obj.informacao}" cols="80" rows="3"
						readonly="#{estagio.readOnly}" id="informacao" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:inputHidden rendered="#{not empty estagio.obj.matricula}" value="#{estagio.obj.matricula.id}"/>
					<h:commandButton id="btConfirmarAcao"
						value="#{estagio.confirmButton}" action="#{estagio.cadastrar}" />
					<h:commandButton id="btConfirmarVoltar"
						value="<< Voltar a tela de consolidação"
						action="#{estagio.voltarDadosGraduacao}" rendered="#{estagio.preCadastroParaGraduacao || estagio.atualizacaoParaGraduacao}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" id="btCancelar" action="#{estagio.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
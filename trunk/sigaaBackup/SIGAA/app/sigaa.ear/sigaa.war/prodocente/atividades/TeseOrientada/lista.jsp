<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<c:if test="${not empty teseOrientadavalidate }" >
	<br />
	<h:graphicImage value="/img/error.gif" style="overflow: visible;"  />
	<h:outputText value="#{teseOrientada.validate}" style="color:red; font-weight: bold;" />
	<br /><br />
</c:if>


<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>

	<h2><ufrn:subSistema /> > Orientações/Monografias</h2>
	<h:outputText value="#{teseOrientada.create}" />

	<h:form id="form" >
		<table class="formulario" style="width:80%">
		<caption class="listagem">Buscar Orientações <c:if test="${teseOrientada.residencia }">- Residência Médica</c:if></caption>

	  	<ufrn:subSistema teste="not portalDocente">
			<tr>
				<td><h:inputHidden id="bServidor" value="true" /></td>
				<th align="right" class="required">Docente:</th>
				<td>
					<h:inputHidden id="id" value="#{teseOrientada.idServidor}"/>
					<h:inputText id="nomeServidor" value="#{teseOrientada.obj.servidor.pessoa.nome}" size="50" /> 
					<ajax:autocomplete
						source="form:nomeServidor" target="form:id"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
						parser="new ResponseXmlToHtmlListParser()" /> 
				</td>
				<td>
					<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox id="bUnidade" value="#{teseOrientada.buscaUnidade}"/>
				</td>
				<td align="right">Departamento:</td>
				<td>
					<h:selectOneMenu value="#{teseOrientada.idUnidade}" style="width: 316px"
						disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}"
						id="departamento">
						<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.allDepartamentoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</ufrn:subSistema>

		<tr>
			<td width="3%">
				<h:selectBooleanCheckbox id="bTipoOrientacao" value="#{teseOrientada.buscaTipoOrientacao}"/>
			</td>
			<td width="20%" align="right"><label for="form:bTipoOrientacao">Tipo de Orientação</label>:</td>
			<td>
				<h:selectOneMenu value="#{teseOrientada.idTipoOrientacao}" style="width: 95%" onclick="marcaCheckBox('form:bTipoOrientacao')"
					disabled="#{teseOrientada.readOnly}" disabledClass="#{teseOrientada.disableClass}"
					id="TipoAtividadeEnsino">
					<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{teseOrientada.tipoOrientacao}" />
				</h:selectOneMenu>
			</td>
		</tr>
	<tfoot>
		<tr>
			 <td colspan="4" align="center">
				<h:commandButton action="#{teseOrientada.buscar}" value="Buscar"/>
			 </td>
		</tr>
	</tfoot>
	</table>
		<br>
			<center><h:graphicImage url="/img/required.gif"
				style="vertical-align: top;" /> <span class="fontePequena">
			Campos de preenchimento obrigatório. </span></center>
		<br>
	</h:form>
	
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			  <c:if test="${teseOrientada.residencia }">
			  	<h:commandLink action="#{teseOrientada.cadastrarResidencia}" value="Cadastrar Nova Orientação - Residência Médica"/>
			  </c:if>
			  <c:if test="${!teseOrientada.residencia}">
			  <h:commandLink action="#{teseOrientada.preCadastrar}" value="Cadastrar Nova Orientação"/>
			  </c:if>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Orientação <c:if test="${teseOrientada.residencia }">- Residência Médica</c:if>
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Orientação <c:if test="${teseOrientada.residencia }">- Residência Médica</c:if><br/>
		</div>
	</h:form>

<c:set var="lista" value="#{teseOrientada.atividades}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span><b><i></>Nenhuma Orientação Encontrada.</i></b></span>
	</center>
	<br />
</c:if>

<c:if test="${not empty lista}">
	<h:form>
	<h:outputText value="#{util.create}"/>

	<table class="listagem">
		<caption class="listagem">Lista de Orientações <c:if test="${teseOrientada.residencia }">- Residência Médica</c:if></caption>
		<thead>
			<tr>
				<td>Título</td>
				<td>Orientando</td>
				<ufrn:subSistema teste="not portalDocente">
					<td>Docente</td>
				</ufrn:subSistema>
				<td>Agência Financiadora</td>
				<td>Tipo de Orientação</td>
				<td align="center">Início</td>
				<td align="center">Data de Defesa</td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<c:forEach items="#{lista}" var="item" varStatus="status">

			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" style="${not empty item.dataPublicacao ? 'font-weight: bold' : ''}">
				<td>${item.titulo}</td>

				<c:if test="${item.discenteExterno}">
					<td>${item.orientando}</td>
				</c:if>
				<c:if test="${not item.discenteExterno}">
				<td>
				    ${item.orientandoDiscente.pessoa.nome}
				     <%-- Os discentes que foram Migrados do Antigo Prodocente NÃO são externos, porém estão cadastrados apenas a string em teseOrientada.orientando --%>
				     <c:if test="${empty item.orientandoDiscente.pessoa.nome  }">
				     ${item.orientando}
				     </c:if>
				</td>
				</c:if>
				<ufrn:subSistema teste="not portalDocente">
					<td>${item.servidor.pessoa.nome}</td>
				</ufrn:subSistema>
				<td>${item.entidadeFinanciadora.nome}</td>
				<td>${item.tipoOrientacao.descricao}</td>
				<td align="center"><ufrn:format valor="${item.periodoInicio}" type="data"/></td>
				<td align="center"><ufrn:format valor="${item.dataPublicacao}" type="data"/></td>

				<td width="20">
					<h:commandLink title="Alterar" action="#{teseOrientada.atualizar}">
						<h:graphicImage url="/img/alterar.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
				<td>
					<h:commandLink title="Remover" action="#{teseOrientada.remover}" onclick="#{confirmDelete}">
						<h:graphicImage url="/img/delete.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</table>
	</h:form>

</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
	<h2>Eleição Para Diretor de Centro</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{voto.listar}"/>
			</div>
			</h:form>
	</center>


	<div id="panel">
	<div id="cadastrar">

	<table class="formulario" width="100%">
		<h:form id="formCandidato">
			<caption class="listagem">Votação</caption>
			<h:inputHidden value="#{candidato.confirmButton}" />
			<h:inputHidden value="#{candidato.obj.id}" />
			<tr>
				<th>Descrição:</th>
				<td><h:inputText size="30" id="descricao"
					value="#{candidato.obj.descricao}"
					readonly="#{candidato.readOnly}" />
					<span class="required">&nbsp;</span>
				</td>
			</tr>
			<tr>
				<th>Chapa:</th>
				<td><h:inputText size="60" maxlength="255" id="chapa"
					value="#{candidato.obj.chapa}"
					readonly="#{candidato.readOnly}" />
					<span class="required">&nbsp;</span>
				</td>
			</tr>
			<tr>
				<th>Servidor:</th>
				<td>
					<h:inputHidden id="id" value="#{candidato.obj.servidor.id}"></h:inputHidden>
					<h:inputText id="nome"	value="#{candidato.obj.servidor.pessoa.nome}" size="50" />
					<ajax:autocomplete source="formCandidato:nome" target="formCandidato:id"
						baseUrl="/sigaa/ajaxServidor" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
						parser="new ResponseXmlToHtmlListParser()" />
					<span class="required">&nbsp;</span>			
				</td>
			</tr>
			<tr>
				<th>Eleição:</th>
				<td>
					<h:selectOneMenu id="eleicao" value="#{candidato.obj.eleicao.id}" >
							<f:selectItem itemValue="0" itemLabel=">> SELECIONE UMA ELEICAO <<"  />
							<f:selectItems value="#{eleicao.allCombo}"/>
						</h:selectOneMenu>
					<span class="required">&nbsp;</span>
				</td>
			</tr>			
			
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{candidato.confirmButton}"
						action="#{candidato.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{candidato.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>

	</div>

	<div id="listar"></div>
	</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
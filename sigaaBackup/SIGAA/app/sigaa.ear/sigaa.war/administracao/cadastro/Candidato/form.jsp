<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
	<h2><ufrn:subSistema /> > Candidato</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{candidato.listar}"/>
			</div>
			</h:form>
	</center>


	<div id="panel">
	<div id="cadastrar">

	<table class="formulario" width="80%">
		<h:form enctype="multipart/form-data" id="formCandidato">
			<caption class="listagem">Cadastro de Candidato</caption>
			<h:inputHidden value="#{candidato.confirmButton}" />
			<h:inputHidden value="#{candidato.obj.id}" />
			<tr>
				<th class="required">Eleição:</th>
				<td>
					<h:selectOneMenu id="eleicao" value="#{candidato.obj.eleicao.id}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UMA ELEICAO --"  />
							<f:selectItems value="#{eleicao.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="30" id="descricao" maxlength="80"
					value="#{candidato.obj.descricao}"
					readonly="#{candidato.readOnly}" />
				</td>
			</tr>
			<tr>
				<th	class="required">Chapa:</th>
				<td><h:inputText size="60" maxlength="255" id="chapa"
					value="#{candidato.obj.chapa}" onkeyup="return formatarInteiro(this);"
					readonly="#{candidato.readOnly}" />
				</td>
			</tr>
			<tr>
				<th class="required">Servidor:</th>
				<td>
					<h:inputHidden id="id" value="#{candidato.obj.servidor.id}"></h:inputHidden>
					<h:inputText id="nome" value="#{candidato.obj.servidor.pessoa.nome}" size="50" />
					<ajax:autocomplete source="formCandidato:nome" target="formCandidato:id"
						baseUrl="/sigaa/ajaxServidor" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
						parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicator" style="display:none; "> <img
							src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			<tr>
				<th>Foto:</th>
				<td>
					<t:inputFileUpload id="foto" value="#{candidato.foto}" size="50"/>
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

		<br>
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
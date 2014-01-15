<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Iniciação Científica</h2>
	<br>
	<h:form id="form">
		<center>
			<table class="formulario" width="70%">
			<caption class="listagem">Buscar Iniciação Cientifica</caption>
			  <tr>
					<td>																				 
					<h:selectBooleanCheckbox id="buscaServidor" value="#{iniciacaoCientifica.buscaServidor}" />
					</td>
					<th>Docente:</th>
	
					<td>
						<h:inputHidden id="idServidor" value="#{iniciacaoCientifica.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{iniciacaoCientifica.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:idServidor"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> </td>
						<td>
						<span id="indicator"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>
						
				</tr>
				<tr>
				<td>
				 <h:selectBooleanCheckbox id="bUnidade" value="#{iniciacaoCientifica.buscaUnidade}"/>
				</td>
					<th>Departamento:</th>

					<td>
					<h:selectOneMenu value="#{iniciacaoCientifica.idUnidade}" style="width: 400px"
					disabled="#{iniciacaoCientifica.readOnly}" disabledClass="#{iniciacaoCientifica.disableClass}"
					id="departamento">
					<f:selectItem itemValue="-1" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
					</h:selectOneMenu></td>
				</tr>	
				
				<tr>
				<td>
				 <h:selectBooleanCheckbox id="bUnidade" value="#{iniciacaoCientifica.buscaUnidade}"/>
				</td>
					<th>Departamento:</th>

					<td>
					<h:selectOneMenu value="#{iniciacaoCientifica.idUnidade}" style="width: 400px"
					disabled="#{iniciacaoCientifica.readOnly}" disabledClass="#{iniciacaoCientifica.disableClass}"
					id="departamento">
					<f:selectItem itemValue="-1" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{unidade.allDepartamentoCombo}" />
					</h:selectOneMenu></td>
				</tr>	
								
				<tr>
					<td align="center" colspan="4"> <h:commandButton actionListener="#{iniciacaoCientifica.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
			</table>
		</center>
	</h:form>
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" /> <h:commandLink action="#{iniciacaoCientifica.preCadastrar}" value="Cadastrar Nova Iniciação Cientifica"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Iniciação Cientifica
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Iniciação Cientifica <br/>
		</div>
	</h:form>
	<h:outputText value="#{iniciacaoCientifica.create}" />
	<table class="listagem">
		<caption class="listagem">Lista de Iniciação Científicas</caption>
		<thead>
			
			
			<td>Instituição</td>
			<td>Departamento</td>
			<td>Nome do Projeto</td>
			<td>Orientando</td>
			<td>Período Início</td>
			<td>Período Fim</td>
			<td>Servidor</td>
			<td>Agência Financiadora</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${iniciacaoCientifica.atividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.instituicao}</td>
				<td>${item.departamento.nome}</td>
				<td>${item.nomeProjeto}</td>
				<td>${item.orientando}</td>
				<td>${item.periodoInicio}</td>
				<td>${item.periodoFim}</td>
				<td>${item.servidor.pessoa.nome}</td>
				<td>${item.entidadeFinanciadora.nome}</td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{iniciacaoCientifica.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{iniciacaoCientifica.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" /></td>
				</h:form>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

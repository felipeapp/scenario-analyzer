<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h2>Avaliação Organização</h2><br>
<h:form id="form" >
		<center>
			<table class="formulario" width="70%">
			<caption class="listagem">Buscar Avaliação Organização</caption>
				<tr>
					
					<th>Servidor:</th>
	
					<td>
						<h:inputHidden id="id" value="#{avaliacaoOrganizacao.idServidor}"/>
						<h:inputText id="nomeServidor"
							value="#{avaliacaoOrganizacao.obj.servidor.pessoa.nome}" size="60" /> <ajax:autocomplete
							source="form:nomeServidor" target="form:id"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=ufrn"
							parser="new ResponseXmlToHtmlListParser()" /> </td>
						<td>
						<span id="indicator"
							style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
						</td>
							
				</tr>
								
				<tr>
					<td align="center" colspan="4"> <h:commandButton actionListener="#{avaliacaoOrganizacao.buscar}" onclick="submit()" value="Buscar"/> </td>
				</tr>
			</table>
		</center>
	</h:form>
	<br/>
	<br/>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{avaliacaoOrganizacao.preCadastrar}" value="Cadastrar Nova Avaliação Organização"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Avaliação Organização<br/>
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Avaliação Organização<br/>
		</div>
	</h:form>
		<h:outputText value="#{avaliacaoOrganizacao.create}"/>
		<table class=listagem style="width:100%" border="1">
			<caption class="listagem"> Lista de Avaliação Organização</caption>
		<thead>
				
				<td>SubArea</td>
				<td>Veiculo</td>
				<td>Local</td>
				<td>Periodo Inicio</td>
				<td>Periodo Fim</td>
				<td>Servidor</td>
				<td>Tipo Avaliacao Organizacao</td>
				<td>Tipo Participacao</td>
				<td></td>
				<td></td>
			</thead>
			<c:forEach items="${avaliacaoOrganizacao.allAtividades}" var="item"varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<td>${item.subArea.nome}</td>
					<td>${item.veiculo}</td>
					<td>${item.local}</td>
					<td>${item.periodoInicio}</td>
					<td>${item.periodoFim}</td>
					
					<td>${item.servidor.pessoa.nome}</td>
					<td>${item.tipoAvaliacaoOrganizacao.descricao}</td>
					<td>${item.tipoParticipacao.descricao}</td>
					
					
					<h:form>
					<td  width=20>
					<input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{avaliacaoOrganizacao.atualizar}"/>
					</td>
					</h:form>
					<h:form>
					<td  width=25>
					<input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/delete.gif" alt="Remover" action="#{avaliacaoOrganizacao.remover}" onclick="javascript:if(confirm('Deseja realmente apagar essa atividade ?')){ return true;} return false; void(0);" />
					</td>
					</h:form>
				</tr>
			</c:forEach>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

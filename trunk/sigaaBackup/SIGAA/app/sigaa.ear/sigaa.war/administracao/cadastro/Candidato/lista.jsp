<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Candidato</h2>

	<h:outputText value="#{eleicao.create}" />
	<center>
			<h:messages/>

			<div class="infoAltRem">
	  			
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{candidato.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<table class=listagem>
	  <caption class="listagem">Lista de Candidatos</caption>
		<thead>
			<tr>
				<td>Servidor</td>
				<td style="text-align: right;">Chapa</td>
				<td>Descrição</td>
				<td>Eleição</td>
				<td colspan="2"></td>
			</tr>
		 </thead>
		<c:forEach items="${candidato.allCandidato}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.servidor.siape} - ${item.servidor.nome}</td>
				<td align="right">${item.chapa}</td>
				<td>${item.descricao}</td>
				<td>${item.eleicao.titulo}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" /> 
						<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{candidato.atualizar}" />
					</h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" /> 
						<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{candidato.remover}" onclick="#{confirmDelete}"/>
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
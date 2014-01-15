<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	td.nomeUsuario {
	}	
</style>

<f:view>
	<a4j:keepAlive beanName="forumMedio" />
	
	<h:form id="listagem">
	<c:set var="foruns" value="#{forumMedio.listaTurmaSerie}"/>

	<h2><ufrn:subSistema /> > Turmas de Ensino Médio </h2>

	<div class="descricaoOperacao">
		<b>Caro Usuário,</b>
		<p>
			Favor selecionar um dos fóruns de turmas do ensino médio, para realizar a criação de um tópico de fórum. 
		</p> 
	</div>
	
	<br/>
	
	<c:if test="${ empty foruns }">
		<center>Nenhum item foi encontrado</center>
		<br/>
		<center> <h:commandButton id="voltar_nao_encontrado" value="<< Voltar" action="#{forumMedio.cancelarForumCursos}" /> </center>
	</c:if>
	
	<c:if test="${ not empty foruns }">
		<table class="listagem">
			<caption>Lista dos tópicos ativos</caption>
			<thead>
				<tr>
					<th>Fórum</th>
					<th colspan=3"></th>
				</tr>
			</thead>
		
		<tbody>
		<c:forEach var="n" items="#{ foruns }" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
				
				<td width="50%">
					${n.descricaoCompleta}
				</td>
				<td align="right" class="icon">
					<h:commandLink id="selecionarForum" title="Selecionar o Fórum" action="#{forumMedio.selecionaForumTurmaMedio}">
						<h:graphicImage alt="Selecionar o Fórum" value="/img/seta.gif"/>
						<f:param name="id_turma_serie" value="#{n.id}" />
					</h:commandLink>
				</td>
				
			</tr>
		</c:forEach>
		</tbody>
			<tfoot>
				<tr>
					<td colspan="21">
						<center> <h:commandButton id="voltar" value="<< Voltar" action="#{forumMedio.cancelarForumCursos}" /> </center>
					</td>
				</tr>
			</tfoot>
	</table>
	
	
	</c:if>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
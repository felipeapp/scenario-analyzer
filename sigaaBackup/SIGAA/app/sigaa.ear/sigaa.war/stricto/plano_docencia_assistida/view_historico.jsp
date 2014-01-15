<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="planoDocenciaAssistidaMBean" />
	<h2> <ufrn:subSistema /> &gt;Histórico de Movimentações do Plano de Docência Assistida </h2>
	
	<table class="visualizacao" style="width: 90%">
		<caption>Dados do Aluno</caption>
		<tr>
			<th>Nome:</th>
			<td>${planoDocenciaAssistidaMBean.obj.discente.matriculaNome}</td>		
		</tr>
		<tr>
			<th>Programa:</th>
			<td>${planoDocenciaAssistidaMBean.obj.discente.unidade.nome}</td>		
		</tr>	
		<tr>
			<th>Orientador:</th>
			<td>${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador}</td>		
		</tr>		
		<tr>
			<th>Nível:</th>
			<td>${planoDocenciaAssistidaMBean.obj.discente.nivelDesc}</td>		
		</tr>
		<tr>
			<th>Ano/Período:</th>
			<td>${planoDocenciaAssistidaMBean.obj.ano}.${planoDocenciaAssistidaMBean.obj.periodo}</td>		
		</tr>
		<tr>
			<th>Situação Atual:</th>
			<td>${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>		
		</tr>						
	</table>		
	
	<br/>
	
	<table class="listagem" style="width: 90%">
		<caption class="listagem">Histórico de Movimentações</caption>
		<c:if test="${not empty planoDocenciaAssistidaMBean.obj.historicoMovimentacoes}">			
			<thead>
				<tr>
					<th style="text-align: center;">Data</th>
					<th>Status</th>
					<th>Usuário</th>
					<th>Observação</th>
				</tr>
			</thead>
			<c:forEach items="#{planoDocenciaAssistidaMBean.obj.historicoMovimentacoes}" var="item" varStatus="loop">				
				<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">	
					<td style="text-align: center;"><ufrn:format type="dataHora" valor="${item.data}"/></td>
					<td>${item.statusDescricao}</td>				
					<td>${item.registroCadastro}</td>		
					<td>${item.observacao}</td>														
				</tr>
			</c:forEach>										
		</c:if>
		<c:if test="${empty planoDocenciaAssistidaMBean.obj.historicoMovimentacoes}">
			<tr>
				<td colspan="9" style="text-align: center;">
					<i>Nenhuma Movimentação encontrada para o Plano Selecionado.</i>
				</td>
			</tr>	
		</c:if>			
	</table>		

	<div class="voltar">
		<a href="javascript:history.back();"> Voltar </a>
	</div>
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	
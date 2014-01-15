<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> &gt; Lista de Resumos do CIC para Avaliação</h2>
	<h:outputText value="#{avaliacaoResumoBean.create}" />
	
	<c:if test="${not empty avaliacaoResumoBean.avaliacoes}">
	<div class="descricaoOperacao">
		<p>
			Selecione um resumo da lista abaixo para visualizar seus dados completos e emitir seu parecer.
			Ao concluir as avaliações, clique no botão ao final da tela para encerrar.
		</p>
	</div>
	
	<br/>
	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Avaliar Resumo<br/>
	</div>
	</center>
	
	<h:form id="form">
		<table class="listagem" width="100%">
			<caption>Resumos do CIC para Avaliação</caption>
			<thead>
					<tr>
						<td>Código</td>
						<td>Autor</td>
						<td>Orientador</td>
						<td colspan="2">Status</td>
					</tr>
			</thead>
			<c:forEach var="av" items="#{avaliacaoResumoBean.avaliacoes}">
				<tr>
					<td>${av.resumo.codigo}</td>
					<td>${av.resumo.autor.nome}</td>
					<td>${av.resumo.orientador.nome}</td>
					<td>${av.resumo.statusString}</td>
					<td  width="5%" style="text-align: right;">
						<h:commandLink title="Avaliar Resumo" style="border: 0;" action="#{avaliacaoResumoBean.popularAvaliacao}">
							<f:param name="id" value="#{av.id}" />
							<h:graphicImage url="/img/seta.gif" alt="Avaliar Resumo" />
						</h:commandLink>
					</td>
				</tr>			
			</c:forEach>

		<tfoot>
			<tr>
				<td colspan="6" style="text-align: center;">	
					<h:commandButton id="btnConcluir" value="Sair da Avaliação de Resumos" action="#{avaliacaoResumoBean.cancelar}" onclick="#{confirmacao}" />
				</td>
			</tr>
		</tfoot>
	</table>
				
	</h:form>
	</c:if>
	
	<c:if test="${empty avaliacaoResumoBean.avaliacoes}">
		<center>
			<br />
			<font color="red" style="font-weight: bold">Não há mais avaliações de resumo destinadas a você.</font>
		</center>
	</c:if>

<c:set var="confirmacao" value="return confirm('Deseja realmente concluir as Avaliações?');" scope="application"/>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
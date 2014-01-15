<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<% CheckRoleUtil.checkRole(request, response, new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }); %>

<f:view>
	<h:form>
		<h2><ufrn:subSistema /> > Metodologias de Avaliação</h2>
	
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
			<h:commandLink action="#{metodologiaAvaliacaoEad.preCadastrar}" value=": Cadastrar">
				<f:param name="idCurso" value="#{metodologiaAvaliacaoEad.cursoSelecionado.id}" />
			</h:commandLink>
			
			<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{metodologiaAvaliacaoEad.anyMetodologiaAtiva }" />
			<h:outputText value=": Inativar" rendered="#{metodologiaAvaliacaoEad.anyMetodologiaAtiva}" />
			
	        <h:graphicImage value="/img/buscar.gif" style="overflow: visible;"/>
	        <h:outputText value=": Visualizar" />
		</div>
	</center>
	
		<table class="listagem">
			<caption>Selecione a metodologia</caption>
			<thead>
				<tr>
					<th>Curso</th>
					<th style="text-align:center">Início</th>
					<th style="text-align:center">Fim</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<c:forEach items="#{metodologiaAvaliacaoEad.metodologias}" var="item" varStatus="status">
			
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">				
					<td>
						<h:outputText value="#{item.curso.nome}"/>
					</td>
					<td align="center">${item.anoPeriodoInicialFormatado}</td>
					<td align="center">${item.anoPeriodoFimFormatado}</td>
					
					<td width="3%" align="right">
						<h:commandLink action="#{metodologiaAvaliacaoEad.visualizar}" >
							<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" title="Visualizar" />
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
					<td width="3%" align="right">
						<h:commandLink action="#{metodologiaAvaliacaoEad.preInativar}" onclick="return confirm('Deseja realmente inativar essa metodologia?');" rendered="#{item.ativa }">
							<h:graphicImage value="/img/check_cinza.png" alt="Inativar" title="Inativar" />
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<tfoot>
				<tr align="center">
					<td colspan="5">
						<h:commandButton id="voltar" value="<< Voltar" action="#{metodologiaAvaliacaoEad.voltar}">
							<f:setPropertyActionListener target="#{metodologiaAvaliacaoEad.operacaoVoltar}" value="3" />
						</h:commandButton>
					<h:commandButton value="Cancelar" action="#{metodologiaAvaliacaoEad.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>				
		</table>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
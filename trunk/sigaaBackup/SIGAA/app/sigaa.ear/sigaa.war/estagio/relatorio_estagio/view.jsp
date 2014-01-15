<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="relatorioEstagioMBean" />
<a4j:keepAlive beanName="buscaEstagioMBean"/>
<h2> <ufrn:subSistema /> &gt; Visualização do Estágio</h2>

<c:set var="estagio" value="#{relatorioEstagioMBean.estagio}"/>
<%@include file="/estagio/estagio/include/_dados_estagio.jsp"%>

<br/>

<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Respostas
		<c:if test="${relatorioEstagioMBean.portalCoordenadorGraduacao}">
			<h:graphicImage value="/img/estagio/validar_relatorio.png" style="overflow: visible;"/>: Validar Relatório
		</c:if>					
	</div>
</center>
	
<h:form id="form">
	<table class="listagem" width="100%">
		<caption>Relatórios do Estágio</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Data de Cadastro</th>
				<th>Tipo do Relatório</th>
				<th>Usuário</th>
				<th colspan="3">Situação</th>
			</tr>	
		</thead>				
		<c:forEach items="#{relatorioEstagioMBean.relatoriosEstagio}" var="relatorio" varStatus="loop">		
			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td style="text-align: center;">
					<ufrn:format type="data" valor="${relatorio.dataCadastro}"/>
				</td>
				<td>
					${relatorio.tipo.descricao}
				</td>				
				<td>
					${relatorio.registroCadastro.usuario.pessoa.nome}
				</td>
				<td>
					${relatorio.status.descricao}
				</td>				
				<td style="width: 1px;">
					<h:commandLink action="#{relatorioEstagioMBean.viewRespostas}" title="Visualizar Respostas">
						<h:graphicImage value="/img/view.gif"/>
						<f:setPropertyActionListener value="#{relatorio}" target="#{relatorioEstagioMBean.obj}"/>
					</h:commandLink>				
				</td>
				<td style="width: 1px;">
					<h:commandLink action="#{relatorioEstagioMBean.validarRelatorio}" title="Validar Relatorio"
					 rendered="#{relatorioEstagioMBean.portalCoordenadorGraduacao && !relatorio.aprovado}">
						<h:graphicImage value="/img/estagio/validar_relatorio.png"/>
						<f:setPropertyActionListener value="#{relatorio}" target="#{relatorioEstagioMBean.obj}"/>
					</h:commandLink>					
				</td>
			</tr>
		</c:forEach>	
		<tfoot>
			<tr>
				<td colspan="6" align="center">
					<h:commandButton value="<< Voltar" action="#{buscaEstagioMBean.telaBusca}" id="btVoltar"/>
				</td>
			</tr>
		</tfoot>											
	</table>	

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
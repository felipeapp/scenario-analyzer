<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="jubilamentoMBean"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Cancelamento por Abandono de Curso &gt; Alunos com Pendências</h2>
<div class="descricaoOperacao">
	<h:graphicImage value="/img/warning.gif" style="overflow: visible; float: left;" />
	<p>Atenção Usuário: os discentes listados abaixo possuem empréstimos ativos na biblioteca. 
	Com o vínculo encerrado, o aluno poderá tomar posse indevidamente de um bem da instituição. 
	Deseja realmente cancelar o vínculo deste(s) discente(s)?</p>
</div>

<div class="infoAltRem">
	<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />: Visualizar Histórico
</div>

<h:form>

<table class="listagem">
	<caption>Discentes encontrados (${fn:length(jubilamentoMBean.discentesComPendencias)})</caption>
	<thead>
		<tr>
			<th style="text-align: center;">Matrícula</th>
			<th>Nome</th>
			<th>Curso</th>
			<th>Nível</th>
			<th>Status</th>
			<th style="text-align: center;">Última Matrícula Válida</th>
			<th></th>
		</tr>
	</thead>
	<c:forEach items="#{jubilamentoMBean.discentesComPendencias}" var="discente" varStatus="loop">
		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td style="text-align: center;"><h:outputText value="#{discente.matricula}" /></td>
			<td><h:outputText value="#{discente.pessoa.nome}"/></td>
			<td><h:outputText value="#{discente.curso.descricao}"/></td>
			<td><h:outputText value="#{discente.nivelDesc}"/></td>
			<td><h:outputText value="#{discente.statusString}"/></td>
			<td style="text-align: center;"><h:outputText value="#{ discente.anoIngresso }.#{ discente.periodoIngresso }" /></td>
			<td>
				<h:commandLink action="#{jubilamentoMBean.verHistorico}" target="_blank" title="Visualizar Histórico" id="btaoVerHistorico">
					<h:graphicImage value="/img/listar.gif"/>
					<f:param name="idDiscente" value="#{discente.id}"/>
				</h:commandLink>			
			</td>
		</tr>
	</c:forEach>	
	<tfoot>
	<tr>
		<td colspan="7" style="text-align: center;">
		<input type="hidden" value="true" name="ignoraPendenciasAdmDae"/>
		<h:commandButton value="<< Voltar" action="#{ jubilamentoMBean.telaDiscentesPassiveisJubilamento }" id="btaoVoltar"/>		
		<h:commandButton value="Cancelar" action="#{ jubilamentoMBean.cancelar }" onclick="#{confirm}" id="btaoCancelar"/>
		<h:commandButton value="Próximo >>" action="#{ jubilamentoMBean.cancelarAlunos }" id="btaoCancelamentoPrograma"/>
		</td>
	</tr>
	</tfoot>
</table>

<br/>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
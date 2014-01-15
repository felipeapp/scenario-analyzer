<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>
<style>
	table.listagem tr td.periodo {
		background: #C4D2EB;
		padding: 3px;
		font-weight: bold;
	}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Reposição de Avaliação &gt; Solicitar de Reposição de Avaliação </h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Aluno, </b>
		</p> 	
		<p>Selecione a Avaliação perdida, para realizar a sua Solicitação de Reposição.</p>		
		<br/>
		<p><b>Art. 101.</b> Impedido de participar de qualquer avaliação, por motivo de caso fortuito ou força
		maior devidamente comprovado e justificado, o aluno tem direito de realizar avaliação de
		reposição.</p>
		<br/>
	</div>	
	
	<div class="infoAltRem" style="font-variant: small-caps;">
			<h:graphicImage value="/img/avancar.gif"style="overflow: visible;"/>: Selecionar Avaliação
	</div>
<h:form>
	<table class="listagem" width="80%">
		<thead>
			<tr>
				<td width="60%">Disciplina</td>
				<td style="text-align:center;">Turma</td>
				<td style="text-align:left;">Docente(s)</td>
				<td> </td>
			</tr>
		</thead>
		<c:set var="idTurma" value="0" />
		<c:forEach items="#{solicitacaoReposicaoProva.listaTurmas}" var="t" varStatus="loop">		
			<c:if test="${loop.first || idTurma != t.id_turma}">
				<tr style="background-color: #C4D2EB">
					<td>${t.codigo} - ${t.nome}</td>
					<td align="center">${ t.codturma }</td>
					<td colspan="2">${t.docentes}</td>
				</tr>				
				<c:set var="idTurma" value="#{t.id_turma}" />
				<tr>
					<td colspan="4">
						<table class="listagem" align="center" style="width: 60%;">			
							<thead>	
							<tr>
								<th style="text-align:left; width: 150px;">Avaliação</th>
								<th style="text-align:center; width: 100px;">Data</th>
								<th style="text-align:left; width: 120px;">Hora</th>
								<th style="width: 30px;"> </th>
							</tr>
							</thead>
						</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${idTurma == t.id_turma}">				
				<tr>
					<td colspan="4">
						<table class="listagem" align="center" style="width: 60%;">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align:left; width: 150px;">
									${t.descricao}
								</td>							
								<td style="text-align:center; width: 100px;">
									<ufrn:format type="data" valor="${t.data}"></ufrn:format>
								</td>
								<td style="text-align:left; width: 120px;">
									${t.hora}
								</td>
								<td style="width: 30px; text-align: right;">
									<h:commandLink action="#{solicitacaoReposicaoProva.iniciarSolicitacao}" id="btAvaliacao" title="Selecionar Avaliação">
										<h:graphicImage value="/img/avancar.gif"/>
										<f:param name="idAvaliacao" value="#{ t.id_avaliacao_data }"/>
									</h:commandLink>					
								</td>
							
							</tr>						
						</table>
					</td>
				</tr>
			</c:if>
			<c:set var="idTurma" value="#{t.id_turma}" />
		</c:forEach>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

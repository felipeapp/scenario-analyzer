<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="parecerDocenciaAssistida" />
	<h2> <ufrn:subSistema /> &gt; Planos de Docência Assistida</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Docente,</b></p><br>
		<p>Através desta opção, será possível acompanhar os planos de docência assistida de alunos de Pós-Graduação
		 que escolheram componentes curriculares da Graduação ao qual o sr(a). leciona.</p>
		 <br>
		<p>Será possível visualizar o Plano ou <b>Registrar um Parecer</b> referente ao mesmo.</p>
	</div>
	
	<h:form id="form" prependId="false">

	<c:set var="planos" value="#{parecerDocenciaAssistida.listagem}"/>
	
	<c:if test="${empty planos}">
		<table class="listagem" style="width: 100%">
			<caption class="listagem">Planos de Docência Assistida Cadastrados</caption>
				<tr>
					<td colspan="9" style="text-align: center;">
						<i>Nenhum Plano de Docência Assistida encontrado.</i>
					</td>
				</tr>
		</table>
	</c:if>
	<c:if test="${not empty planos}">
		<center>
			<div class="infoAltRem">
				<h:form>			
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Docência Assistida						
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Registrar Parecer
				</h:form>
			</div>
		</center>
		<c:if test="${not empty planos}">
			<table class="listagem" style="width: 100%">
				<caption class="listagem">Planos de Docência Assistida Cadastrados (${fn:length(planos)})</caption>
				<thead>
					<tr>
						<th style="width:1px;"></th>
						<th>Discente</th>
						<th>Programa</th>
						<th>Componente Curricular</th>
						<th>Nível</th>
						<th style="text-align: center;">Ano/Período</th>
						<th>Status</th>
						<th colspan="2"></th>
					</tr>
				</thead>
				<c:forEach items="#{planos}" var="plano" varStatus="loop">	
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>
							<c:if test="${plano.possuiIndicacao }">
								<h:graphicImage value="/img/star.png" title="Possui indicação de Bolsa REUNI"/>
							</c:if>
						</td>
						<td>${plano.discente.matriculaNome}</td>
						<td>${plano.discente.gestoraAcademica.nome}</td>
						<td>${(empty(plano.componenteCurricular) ? '** NÃO INFORMADO **' : plano.componenteCurricular.codigoNome)}</td>
						<td>${plano.discente.nivelDesc}</td>
						<td style="text-align: center;">${plano.ano}.${plano.periodo}</td>
						<td>
							<c:choose>
								<c:when test="${plano.reprovado || not plano.ativo}">
									<span style="color:red; font-weight: bold;">${plano.descricaoStatus}</span>
								</c:when>
								<c:when test="${plano.aprovado}">
									<span style="color:green; font-weight: bold;">${plano.descricaoStatus}</span>
								</c:when>
								<c:when test="${plano.concluido}">
									<span style="color:#4169E1; font-weight: bold;">${plano.descricaoStatus}</span>
								</c:when>
								<c:otherwise>
									<span style="color:black; font-weight: normal;">${plano.descricaoStatus}</span>
								</c:otherwise>						
							</c:choose>		
						</td>
						<td>					
							<h:commandLink title="Visualizar Plano de Docência Assistida" id="btnvisualizarPlano_${plano.id}" action="#{planoDocenciaAssistidaMBean.viewImpressao}">
								<h:graphicImage value="/img/view.gif"/>
								<f:param name="relatorioSemestral" value="false"/>
								<f:setPropertyActionListener value="#{plano}" target="#{planoDocenciaAssistidaMBean.obj}"/>
							</h:commandLink>										
						</td>									
						<td width="1px">
							<c:if test="${plano.submetido}">
								<h:commandLink title="Registrar Parecer" action="#{parecerDocenciaAssistida.iniciarParecer}"
									rendered="#{plano != null && plano.ativo && plano.submetido && parecerDocenciaAssistida.portalDocente}">
									<h:graphicImage value="/img/seta.gif"/>
									<f:param name="id" value="#{plano.id}"/>
								</h:commandLink>
							</c:if>																
						</td>										
					</tr>					
				</c:forEach>
			</table>
		</c:if>	

	</c:if>
</h:form>		
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	
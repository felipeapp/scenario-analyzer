<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Lista de Planos de Docência Assistida Cadastrados</h2>
	
	<div id="parametrosRelatorio">
	<table>
		<c:if test="${acesso.ppg || acesso.membroApoioDocenciaAssistida}">
			<c:if test="${planoDocenciaAssistidaMBean.filtroUnidade}">
				<tr>
					<th>Programa:</th>
					<td>
						${planoDocenciaAssistidaMBean.unidade.id > 0 ? planoDocenciaAssistidaMBean.unidade.nome : 'Todos'}
					</td>	
				</tr>
			</c:if>
		</c:if>
		<c:if test="${!acesso.ppg && !acesso.membroApoioDocenciaAssistida}">
			<tr>
				<th>Programa:</th>
				<td>${planoDocenciaAssistidaMBean.programaStricto}</td>		
			</tr>	
		</c:if>
		<c:if test="${planoDocenciaAssistidaMBean.filtroStatus}">
			<tr>
				<th>Status:</th>
				<td>
					${planoDocenciaAssistidaMBean.status > 0 ? planoDocenciaAssistidaMBean.descricaoStatus : 'Todos'}
				</td>
			</tr>
		</c:if>		
		<c:if test="${planoDocenciaAssistidaMBean.filtroNivel}">
			<tr>
				<th>Nível:</th>
				<td>
					${planoDocenciaAssistidaMBean.nivel != null &&  planoDocenciaAssistidaMBean.nivel != '0' ? planoDocenciaAssistidaMBean.descricaoNivel : 'Todos'}
				</td>
			</tr>
		</c:if>	
		
		<c:if test="${planoDocenciaAssistidaMBean.filtroModalidade}">
			<tr>
				<th>Modalidade da Bolsa:</th>
				<td>
					${planoDocenciaAssistidaMBean.modalidadeBolsa.id > 0 ? planoDocenciaAssistidaMBean.modalidadeBolsa.descricao : 'Todos'}
				</td>
			</tr>
		</c:if>	
			
		<c:if test="${planoDocenciaAssistidaMBean.filtroTipoBolsa}">
			<tr>
				<th>Tipo do Plano:</th>
				<td><h:outputText value="#{planoDocenciaAssistidaMBean.descricaoTipoBolsa}"/></td>
			</tr>
		</c:if>		
		
		<c:if test="${planoDocenciaAssistidaMBean.filtroAnoPeriodo}">
			<tr>
				<th>Ano-Período:</th>
				<td><h:outputText value="#{planoDocenciaAssistidaMBean.ano} - #{planoDocenciaAssistidaMBean.periodo}"/></td>
			</tr>
		</c:if>
	</table>
	</div>
<br/>
	<c:set var="planos" value="#{planoDocenciaAssistidaMBean.planosSemIndicacao}"/>
	<h2>Planos de Docência Assistida Cadastrados (${fn:length(planos)})</h2>
	<h5>* Alunos com Indicação de Bolsa REUNI.</h5>
	<table class="tabelaRelatorioBorda" style="width: 100%">
		<thead>
			<tr>
				<th style="text-align: center;" colspan="2">Matrícula</th>
				<th>Discente</th>
				<th>Componente Curricular</th>
				<c:if test="${!planoDocenciaAssistidaMBean.filtroNivel}">
					<th>Nível</th>
				</c:if>
				<c:if test="${!planoDocenciaAssistidaMBean.filtroModalidade}">
					<th>Modalidade</th>
				</c:if>				
				<c:if test="${!planoDocenciaAssistidaMBean.filtroAnoPeriodo}">
					<th style="text-align: center;" title="Ano/Período">Ano/Per.</th>
				</c:if>
				<c:if test="${!planoDocenciaAssistidaMBean.filtroStatus}">
					<th>Status</th>
				</c:if>
			</tr>
		</thead>		
		<c:set var="varUnidade" value="0"/>
		<tbody>
		<c:forEach items="#{planos}" var="plano" varStatus="loop">
			<c:if test="${!planoDocenciaAssistidaMBean.filtroUnidade}">				
				<c:if test="${varUnidade != plano.discente.gestoraAcademica.id}">
					<tr>
						<td colspan="8" style="font-weight: bold; text-align: center; border-top: 2px solid black; border-bottom: 2px solid black;">${plano.discente.gestoraAcademica.nome}</td>
					</tr>
					<c:set var="varUnidade" value="${plano.discente.gestoraAcademica.id}"/>		
				</c:if>	
			</c:if>				
			<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td style="width: 1px;">
					<c:if test="${plano.possuiIndicacao }">
						<b>*</b>
					</c:if>
				</td>
				<td style="text-align: center;">${plano.discente.matricula}</td>
				<td>${plano.discente.nome}</td>
				<td>${(empty plano.componenteCurricular ? "** NÃO INFORMADO **" : plano.componenteCurricular.codigoNome)}</td>
				<c:if test="${!planoDocenciaAssistidaMBean.filtroNivel}">
					<td>${plano.discente.nivelDesc}</td>
				</c:if>
				<c:if test="${!planoDocenciaAssistidaMBean.filtroModalidade}">
					<td>${plano.modalidadeBolsa.descricao}</td>
				</c:if>					
				<c:if test="${!planoDocenciaAssistidaMBean.filtroAnoPeriodo}">
					<td style="text-align: center;">${plano.ano}.${plano.periodo}</td>
				</c:if>
				<c:if test="${!planoDocenciaAssistidaMBean.filtroStatus}">
					<c:if test="${not plano.ativo}">
						<td>INATIVO</td>
					</c:if>
					<c:if test="${plano.ativo}">
						<td><b>${plano.descricaoStatus}</b></td>
					</c:if>
				</c:if>							
			</tr>					
		</c:forEach>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
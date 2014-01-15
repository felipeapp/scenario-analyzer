<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Sumário de Índices Acadêmicos por Curso de Graduação</h2>
		
	<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Ano Inicial: </th>
			<td> ${relatorioDiscente.ano}</td>
		</tr>
		<tr>
			<th>Ano Final: </th>
			<td> ${relatorioDiscente.anoFinal}</td>
		</tr>
		<tr>
			<th>Centro/Unidade Acadêmica Especializada: </th>
			<td> 
				<h:outputText value="#{ relatorioDiscente.unidade.nome }" rendered="#{ relatorioDiscente.unidade.id > 0 }"/>
				<h:outputText value="TODOS" rendered="#{ relatorioDiscente.unidade.id == 0 }"/>
			</td>
		</tr>
		<tr>
			<th>Modalidade de Educação: </th>
			<td> 
				<h:outputText value="#{ relatorioDiscente.modalidadeEnsino.descricao }" rendered="#{ relatorioDiscente.modalidadeEnsino.id > 0 }"/>
				<h:outputText value="TODOS" rendered="#{ relatorioDiscente.modalidadeEnsino.id == 0 }"/>
			</td>
		</tr>
		<c:if test="${ relatorioDiscente.somenteCursosConvenio }">
		<tr>
			<th colspan="2">Somente cursos que possuem convênio</th>
		</tr>
		</c:if>
		</table>
	</div>
	<br/>
    
	<c:forEach items="#{relatorioDiscente.municipios}" var="_municipio" varStatus="status">
		<!-- Discentes -->
		<table class="tabelaRelatorioBorda" width="100%">
			<caption>MUNICÍPIO: ${_municipio}</caption>
			<thead>
				<tr>
					<th rowspan="2" width="35%">Curso</th>
					<th rowspan="2" style="text-align: right;">Alunos Ativos</th>
					<th colspan="5" style="text-align: center;">Forma de Ingresso</th>
				</tr>
				<tr>
					<th style="text-align: right;">Vestibular</th>
					<th style="text-align: right;">Transf. Voluntária</th>
					<th style="text-align: right;">Portador de Diploma</th>
					<th style="text-align: right;">Reingresso Automático</th>
					<th style="text-align: right;">Outras</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="#{relatorioDiscente.lista}" var="linha" varStatus="status">
				<c:if test="${ linha.curso_cidade eq _municipio }">
					<tr>
						<td>${linha.curso}</td>
						<td style="text-align: right;">${linha.qtd_ativos}</td>
						<td style="text-align: right;">${linha.qtd_forma_ingresso_via_vestibular}</td>
						<td style="text-align: right;">${linha.qtd_forma_ingresso_via_transferencia_voluntaria }</td>
						<td style="text-align: right;">${linha.qtd_forma_ingresso_via_portador_diploma}</td>
						<td style="text-align: right;">${linha.qtd_forma_ingresso_via_reingresso_automatico}</td>
						<td style="text-align: right;">${linha.qtd_forma_ingresso_via_outras_formas_de_ingresso}</td>
					</tr>
				</c:if>
			</c:forEach>
			</tbody>
		</table>
		<!-- Índices -->
		<c:set var="contemDados" value="${ false }"/>
		<c:forEach items="#{relatorioDiscente.sumarioIndices}" var="linha">
			<c:if test="${ linha.curso_cidade eq _municipio }">
				<c:set var="contemDados" value="${ true }"/>
			</c:if>
		</c:forEach>
		<c:if test="${ contemDados }">
		<table class="tabelaRelatorioBorda" width="100%">
			<thead>
				<tr>
					<th rowspan="2" width="35%">Curso</th>
					<th colspan="${fn:length(relatorioDiscente.indices)}" style="text-align: center;">Média do Índice Acadêmico</th>
				</tr>
				<tr>
					<c:forEach items="#{relatorioDiscente.indices}" var="_indice">
						<th style="text-align: right;">${_indice.sigla}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="#{relatorioDiscente.sumarioIndices}" var="linha" varStatus="status">
				<c:if test="${ linha.curso_cidade eq _municipio }">
					<tr>
						<td>${linha.curso}</td>
						<c:forEach items="#{relatorioDiscente.indices}" var="_indice">
							<c:if test="${not empty linha.medias}">
								<c:forEach items="#{linha.medias}" var="_media">
									<c:if test="${_media.key eq _indice.sigla}">		
										<td style="text-align: right;">
											<ufrn:format type="valor4" valor="${_media.value}" />
										</td>
									</c:if>
								</c:forEach>
							</c:if>
							<c:if test="${empty linha.medias}">
								<td style="text-align: right;">0</td>
							</c:if>
						</c:forEach>
					</tr>
				</c:if>
			</c:forEach>
			</tbody>
		</table>
		</c:if>
		<!-- Trancamentos -->
		<c:set var="contemDados" value="${ false }"/>
		<c:forEach items="#{relatorioDiscente.sumarioTrancamento}" var="linha">
			<c:if test="${ linha.curso_cidade eq _municipio }">
				<c:set var="contemDados" value="${ true }"/>
			</c:if>
		</c:forEach>
		<c:if test="${ contemDados }">
		<table class="tabelaRelatorioBorda" width="100%">
			<thead>
				<tr>
					<th rowspan="2" width="35%">Curso</th>
					<th colspan="${fn:length(relatorioDiscente.anosSumario)}" style="text-align: center;">Turmas Trancadas no Ano</th>
					<th colspan="${fn:length(relatorioDiscente.anosSumario)}" style="text-align: center;">Programas Trancados no Ano</th>
				</tr>
				<tr>
					<c:forEach items="#{relatorioDiscente.anosSumario}" var="_ano" >
						<th style="text-align: right;">${_ano}</th>
					</c:forEach>
					<c:forEach items="#{relatorioDiscente.anosSumario}" var="_ano" >
						<th style="text-align: right;">${_ano}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="#{relatorioDiscente.sumarioTrancamento}" var="linha" varStatus="status">
				<c:if test="${ linha.curso_cidade eq _municipio }">
					<tr>
						<td>${linha.curso}</td>
						<c:forEach items="#{relatorioDiscente.anosSumario}" var="_ano">
							<c:if test="${not empty linha.trancamentos}">
								<c:forEach items="#{linha.trancamentos}" var="_trancamento">
									<c:if test="${_trancamento.key eq _ano}">		
										<td style="text-align: right;">${_trancamento.value}</td>
									</c:if>
								</c:forEach>
							</c:if>
							<c:if test="${empty linha.trancamentos}">
								<td style="text-align: right;">0</td>
							</c:if>
						</c:forEach>
						<c:forEach items="#{relatorioDiscente.anosSumario}" var="_ano">
							<c:if test="${not empty linha.trancamentos_programa}">
								<c:forEach items="#{linha.trancamentos_programa}" var="_trancamento">
									<c:if test="${_trancamento.key eq _ano}">		
										<td style="text-align: right;">${_trancamento.value}</td>
									</c:if>
								</c:forEach>
							</c:if>
							<c:if test="${empty linha.trancamentos_programa}">
								<td style="text-align: right;">0</td>
							</c:if>
						</c:forEach>
					</tr>
				</c:if>
			</c:forEach>
			</tbody>
		</table>
		</c:if>
		<br/>
		<br/>
	</c:forEach>
	<br/>
	<b>Legenda</b>
	<ul>
		<c:forEach items="#{relatorioDiscente.indices}" var="_indice">
		<li><b>${_indice.sigla}</b> - ${_indice.descricao}</li>
		</c:forEach>
	</ul>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
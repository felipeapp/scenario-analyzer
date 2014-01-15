<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema /> &gt; Importação de Planilha</h2>

	<div class="descricaoOperacao" style="width:70%;">
		<p>Selecione o arquivo contendo a planilha a ser importada com as notas para a turma <strong>${ consolidarTurma.turma.descricaoSemDocente }</strong> e clique em <strong>importar</strong>.</p>
		<p>O arquivo deverá estar no formato .xls, na versão Microsoft Excel 97/2000/XP.</p>
		<p>A estrutura da planilha não deve ser modificada, ou seja, linhas e colunas não devem ser adicionadas ou removidas.</p>
		<p>Se o arquivo estiver correto, a planilha importada será exibida abaixo do formulário.</p>
	</div>
	
	<h:form id="importar" enctype="multipart/form-data">
		<table class="formulario" style="width:60%;">
			<caption>Selecione o arquivo contendo a planilha a importar</caption>
			<tbody>
				<tr>
					<th class="obrigatorio" style="width:110px;">Arquivo:</th>
					<td><t:inputFileUpload value="#{consolidarTurma.arquivoPlanilha}" id="arquivo" size="60" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton action="#{consolidarTurma.importarPlanilha}" value="Importar" /> 
						<h:commandButton action="#{ consolidarTurma.voltarLancarNotas }" value="<< Voltar" />
						<h:commandButton action="#{ consolidarTurma.cancelar }" value="Cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="required-items" style="text-align:center;">
			<span class="required">&nbsp;</span>
			Itens de Preenchimento Obrigatório
		</div>

	</h:form>
	
	<c:if test="${not empty consolidarTurma.nomesAvaliacoes}">
	
		<div class="descricaoOperacao">
			<p>Caso a importação esteja correta, clique em <strong>Confirmar</strong>, abaixo da planilha. Caso contrário, selecione outro arquivo ou entre em contato através do link <strong>Abrir Chamado</strong>.</p>
		</div>
	
		<h:form id="confirmar">
			<table class="listagem" style="width:80%;">
				<caption>Confirmação dos Dados</caption>
				<thead>
					<tr>
						<th style="text-align:center;">Matrícula</th>
						<th style="text-align:left;">Nome</th>
						<c:set var="colunas" value="2" />
						<c:forEach var="na" items="#{consolidarTurma.nomesAvaliacoes}">
							<th style="text-align:right;">${na}</th>
							<c:set var="colunas" value="${colunas + 1}" />
						</c:forEach>
					</tr>					
				</thead>
				<tbody>
					<c:if test="${ not consolidarTurma.conceito && not consolidarTurma.competencia}">
						<c:forEach var="m" items="#{consolidarTurma.matriculasNotas}" varStatus="status">
							<tr class='linha${status.index % 2 == 0 ? "P" : "Imp"}ar'>
								<td style="text-align:center;">${m.key}</td>
								<td style="text-align:left;width:250px;"><div style="width:260px;overflow:hidden;">${consolidarTurma.matriculasNomesImportacao[m.key]}</div></td>
									<c:forEach var="n" items="#{m.value}">
										<td style="text-align:right;">${n}</td>
									</c:forEach>	
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${ consolidarTurma.conceito }">
						<c:forEach var="m" items="#{consolidarTurma.matriculasConceito}" varStatus="status">
								<tr class='linha${status.index % 2 == 0 ? "P" : "Imp"}ar'>
									<td style="text-align:center;">${m.key}</td>
									<td style="text-align:left;width:250px"><div style="width:260px;overflow:hidden;">${consolidarTurma.matriculasNomesImportacao[m.key]}</div></td>
										<c:forEach var="n" items="#{m.value}">
												<td style="text-align:right;">${n}</td>
										</c:forEach>
								</tr>
						</c:forEach>
					</c:if>
					<c:if test="${ consolidarTurma.competencia }">
						<c:forEach var="m" items="#{consolidarTurma.matriculasCompetencia}" varStatus="status">
								<tr class='linha${status.index % 2 == 0 ? "P" : "Imp"}ar'>
									<td style="text-align:center;">${m.key}</td>
									<td style="text-align:left;width:250px;"><div style="width:260px;overflow:hidden;">${consolidarTurma.matriculasNomesImportacao[m.key]}</div></td>
										<c:forEach var="n" items="#{m.value}">
												<td style="text-align:right;">${n}</td>
										</c:forEach>
								</tr>
						</c:forEach>
					</c:if>
				</tbody>
				<tfoot>
					<tr>
						<td style="text-align:center;" colspan="${colunas}">
							<h:commandButton action="#{consolidarTurma.confirmarImportacaoPlanilha}" value="Confirmar" />
							<h:commandButton action="#{ consolidarTurma.voltarLancarNotas }" value="<< Voltar" />
							<h:commandButton action="#{consolidarTurma.cancelar }" value="Cancelar" onclick="#{confirm}" />
						</td>
					</tr>
				</tfoot>
			</table>
		</h:form>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<h2>Visualização de Plano de Trabalho</h2>


<h:form id="formPlanoTrabalho">

<c:set var="plano" value="${planoTrabalhoProjeto.obj}" />

<table class="tabelaRelatorio" width="100%">
	<caption> Plano de Trabalho de Discente do Projeto </caption>
	<tbody>
		<tr>
			<th width="20%"><b> Ano:</b> </th>
			<td> ${planoTrabalhoProjeto.obj.projeto.ano}</td>
		</tr>
		<tr>
			<th width="20%"><b> Título da Ação:</b> </th>
			<td>${planoTrabalhoProjeto.obj.projeto.titulo} </td>
		</tr>
		
		<tr>
			<th><b> Coordenador(a):</b> </th>
			<td> ${planoTrabalhoProjeto.obj.projeto.coordenador.pessoa.nome }</td>
		</tr>
		<c:if test="${not empty planoTrabalhoProjeto.obj.discenteProjeto}">
				<tr>
					<th><b> Discente:</b> </th>
					<td> ${planoTrabalhoProjeto.obj.discenteProjeto.discente.matriculaNome }</td>
				</tr>

				<tr>
					<th><b> Curso do Discente:</b> </th>
					<td> ${planoTrabalhoProjeto.obj.discenteProjeto.discente.curso.nomeCompleto }</td>
				</tr>

				<tr>
					<th><b>Tipo de Vínculo:</b></th>
					<td><h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.tipoVinculo.descricao}" /></td>
				</tr>

				<tr>
					<th><b>Situação:</b></th>
					<td><h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.situacaoDiscenteProjeto.descricao}" /></td>
				</tr>
		</c:if>
		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center">Corpo do Plano de Trabalho:</td>
		</tr>
		
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Período de execução:</b> </th>
		</tr>
		<tr>
			<td colspan="2" style="text-align: justify;"> <p><fmt:formatDate value="${planoTrabalhoProjeto.obj.dataInicio}" pattern="dd/MM/yyyy"/> a <fmt:formatDate value="${planoTrabalhoProjeto.obj.dataFim}" pattern="dd/MM/yyyy"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Objetivos:</b> </th>
		</tr>
		<tr>
			<td colspan="2" style="text-align: justify;"> <p><ufrn:format type="texto" name="planoTrabalhoProjeto" property="obj.objetivos"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Justificativa:</b> </th>
		</tr>
		<tr>
			<td colspan="2" style="text-align: justify;"> <p><ufrn:format type="texto" name="planoTrabalhoProjeto" property="obj.justificativa"/></p></td>
		</tr>
		<tr>
			<th colspan="2" style="text-align: left;"> <b>Descrição da(s) Metodologia/Atividades desenvolvida(s):</b> </th>
		</tr>
		<tr>
			<td colspan="2" style="text-align: justify;"> <p><ufrn:format type="texto" name="planoTrabalhoProjeto" property="obj.metodologia"/></p></td>
		</tr>

		<tr>
			<th colspan="2" style="text-align: left;"> <b>Local de Trabalho do Discente:</b> </th>
		</tr>
		<tr>
			<td colspan="2"> <p><ufrn:format type="texto" name="planoTrabalhoProjeto" property="obj.localTrabalho"/></p></td>
		</tr>

		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center">Cronograma de Atividades</td>
		</tr>

		<tr> <td colspan="2" style="margin:0; padding: 0;">
			<div style="overflow: auto; width: 100%">
			<table id="cronograma" class="subFormulario" width="100%">
			<thead>
				<tr>
					<th width="30%" rowspan="2"> Atividade </th>
					<c:forEach items="${planoTrabalhoProjeto.telaCronograma.mesesAno}" var="ano">
					<th colspan="${fn:length(ano.value)}" style="text-align: center" class="inicioAno fimAno">
						${ano.key}
					</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${planoTrabalhoProjeto.telaCronograma.mesesAno}" var="ano">
						<c:forEach items="${ano.value}" var="mes" varStatus="status">
						<c:set var="classeCabecalho" value=""/>
						<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
						<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>

						<th class="${classeCabecalho}" style="text-align: center"> ${mes}	</th>
						</c:forEach>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades" value="${fn:length(planoTrabalhoProjeto.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes" value=",${fn:join(planoTrabalhoProjeto.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
				<tr>
					<th> ${planoTrabalhoProjeto.telaCronograma.atividade[statusAtividades.index-1]} </th>
					<c:forEach items="${planoTrabalhoProjeto.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
						<c:forEach items="${ano.value}" var="mes">
							<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
							<c:set var="classeCelula" value=""/>
							<c:if test="${ fn:contains(valoresCheckboxes, valorCheckbox) }">
								<c:set var="classeCelula" value="selecionado"/>
							</c:if>
							<td align="center" class="${classeCelula}" >
								&nbsp;
							</td>
						</c:forEach>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>
			</table>
			 </div>
			</td>
		</tr>


		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center">Histórico de Discentes</td>
		</tr>

		<tr> 
			<td colspan="2" style="margin:0; padding: 0;">
				<table id="cronograma" class="subFormulario" width="100%">
					<thead>
						<tr>
							<th> Discente </th>
							<th style="text-align: center"> Início </th>
							<th style="text-align: center"> Fim </th>
							<th> Vínculo </th>							
							<th> Situação </th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="membro" items="${ planoTrabalhoProjeto.obj.historicoDiscentesPlano }" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td> ${membro.discente.matriculaNome} </td>
								<td style="text-align: center"> <ufrn:format type="data" name="membro" property="dataInicio"/> </td>
								<td style="text-align: center"> <ufrn:format type="data" name="membro" property="dataFim"/> </td>
								<td> ${membro.tipoVinculo.descricao}</td>
								<td> ${membro.situacaoDiscenteProjeto.descricao}</td>
							</tr>
						</c:forEach>
					
					</tbody>
				</table>
			</td>
		</tr>
	
		<c:if test="${empty planoTrabalhoProjeto.obj.historicoDiscentesPlano}">
			<tr>
				<td colspan="2">
						<p style="padding: 10px 100px; text-align: center; color: #F00; background-color: #FDFAF6;">
								Histórico de Discentes vazio!
						</p>
				</td>
			</tr>
		</c:if>
		
	</tbody>
	
	<c:if test="${planoTrabalhoProjeto.confirmButton == 'Remover'}">
		<tfoot>
			<tr>
				<td colspan="2">
					<br/>
					<center><h:commandButton value="Remover" action="#{planoTrabalhoProjeto.cadastrar}" id="btRemover" /></center>
					<br/>
				</td>
			</tr>
		</tfoot>
	</c:if>
</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="processamentoMatriculaDiscenteSerie"/>
<h2> <ufrn:subSistema /> &gt; Resultado do Processamento de Consolidação de Discentes em Série</h2>

<h:form id="form">

	<div class="descricaoOperacao">
		<p>
			Resultado do Processamento de Consolidação de Discentes por Série, 
			baseando nas disciplinas pertencentes e pagas pelo discente no ano selecionado. 
		</p>
	</div>
	
	<table class="visualizacao" style="width: 80%">
		<tr>
			<th width="3%" style="text-align: right;">Ano:</th>
			<td style="text-align: left;">${processamentoMatriculaDiscenteSerie.ano }</td>
		</tr>
		<c:if test="${processamentoMatriculaDiscenteSerie.curso.id > 0 }">
		<tr>
			<th width="3%" style="text-align: right;">Curso:</th>
			<td style="text-align: left;">${processamentoMatriculaDiscenteSerie.curso.nome }</td>
		</tr>
		</c:if>
	</table>
	<br/>
	<table class="listagem" style="width: 90%; border-width: 0; border: none; font-size: 1.0em;" align="center">
		<tbody>
			<c:set var="var_curso" value="0"/>
			<c:set var="totalMatriculado" value="0"/>
			<c:set var="totalAprovado" value="0"/>
			<c:set var="totalReprovado" value="0"/>
			<c:set var="totalCancelado" value="0"/>
			<c:set var="totalTrancado" value="0"/>
			<c:set var="totalAprovDepend" value="0"/>
			<c:forEach items="${processamentoMatriculaDiscenteSerie.listaSerie}" var="row_serie" varStatus="loop">
				<c:if test="${var_curso != row_serie.cursoMedio.id}">
					<tr>
						<td colspan="2">
							<h3 class="tituloTabela" style="margin-top: 10px;">
								${row_serie.cursoMedio.nome}
							</h3>
						</td>
					</tr>
					<c:set var="var_curso" value="${row_serie.cursoMedio.id}"/>
				</c:if>
				<tr>		
					<td colspan="2" class="titulo">
						<h3 style="text-align: center; background-color: #C8D5EC; padding: 3px 0 3px 0; margin: 0 auto;">
							<b>Série:</b> ${row_serie.numeroSerieOrdinal}
						</h3>
					</td>
				</tr>
					<c:forEach items="${row_serie.turmas}" var="turma">
						
						<tr>
							<td colspan="2">
							<table class="subListagem" style="width: 100%">
								<caption>Turma: ${turma.nome == 'dep' ?'Dependência':turma.nome}</caption>
								<c:set var="qtdMatriculado" value="0"/>
								<c:set var="qtdAprovado" value="0"/>
								<c:set var="qtdReprovado" value="0"/>
								<c:set var="qtdCancelado" value="0"/>
								<c:set var="qtdTrancado" value="0"/>
								<c:set var="qtdAprovDepend" value="0"/>
								<thead>
									<tr>
										<th width="65%" colspan="4">Discente</th>
										<th width="15%" style="text-align: center">Dependência</th>
										<th width="20%">Situação</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${turma.alunos}" var="aluno" varStatus="status">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											<td colspan="4">${aluno.discenteMedio.matriculaNome}</td>
											<td align="center"><ufrn:format type="simnao" valor="${aluno.dependencia}"/> </td>
											<td>${aluno.situacaoMatriculaSerie.descricao}</td>
											<c:if test="${aluno.situacaoMatriculaSerie.descricao eq 'MATRICULADO'}">
												<c:set var="qtdMatriculado" value="${qtdMatriculado + 1}"/>
											</c:if>
											<c:if test="${aluno.situacaoMatriculaSerie.descricao eq 'APROVADO'}">
												<c:set var="qtdAprovado" value="${qtdAprovado + 1}"/>
											</c:if>
											<c:if test="${aluno.situacaoMatriculaSerie.descricao eq 'REPROVADO'}">
												<c:set var="qtdReprovado" value="${qtdReprovado + 1}"/>
											</c:if>
											<c:if test="${aluno.situacaoMatriculaSerie.descricao eq 'CANCELADO'}">
												<c:set var="qtdCancelado" value="${qtdCancelado + 1}"/>
											</c:if>
											<c:if test="${aluno.situacaoMatriculaSerie.descricao eq 'TRANCADO'}">
												<c:set var="qtdTrancado" value="${qtdTrancado + 1}"/>
											</c:if>
											<c:if test="${aluno.situacaoMatriculaSerie.descricao eq 'APROVADO EM DEPENDÊNCIA'}">
												<c:set var="qtdAprovDepend" value="${qtdAprovDepend + 1}"/>
											</c:if>
										</tr>
									</c:forEach>
									<tr style="background-color:#DDD; font-weight: bold; font-size: 10px">
										<td align="left">Matriculado: ${ qtdMatriculado }</td>
										<td align="left">Aprovado: ${ qtdAprovado }</td>
										<td align="left">Reprovado: ${ qtdReprovado }</td>
										<td align="left">Cancelado: ${ qtdCancelado }</td>
										<td align="left">Trancado: ${ qtdTrancado }</td>
										<td align="left">Aprovado em dependência: ${ qtdAprovDepend }</td>
									</tr>
									
									<c:set var="totalMatriculado" value="${totalMatriculado + qtdMatriculado}"/>
									<c:set var="totalAprovado" value="${totalAprovado + qtdAprovado}"/>
									<c:set var="totalReprovado" value="${totalReprovado + qtdReprovado}"/>
									<c:set var="totalCancelado" value="${totalCancelado + qtdCancelado}"/>
									<c:set var="totalTrancado" value="${totalTrancado + qtdTrancado}"/>
									<c:set var="totalAprovDepend" value="${totalAprovDepend + qtdAprovDepend}"/>
								</tbody>
							</table>
							</td>
						</tr>	
					</c:forEach>
			</c:forEach>
			
			<tr>
				<td colspan="2">
					<table class="listagem" style="width: 50%; margin-top: 20px; font-weight: bold; font-size: 10px" >
						<tr>
							<td colspan="2">
								<h3 class="tituloTabela">
									Resultado Quantitativo do Processamento
								</h3>
							</td>
						</tr>
						<tr class="linhaPar"><td align="left">Matriculado</td><td align="right">${ totalMatriculado }</td></tr>
						<tr class="linhaImpar"><td align="left">Aprovado</td><td align="right">${ totalAprovado }</td></tr>
						<tr class="linhaPar"><td align="left">Reprovado</td><td align="right">${ totalReprovado }</td></tr>
						<tr class="linhaImpar"><td align="left">Cancelado</td><td align="right">${ totalCancelado }</td></tr>
						<tr class="linhaPar"><td align="left">Trancado</td><td align="right">${ totalTrancado }</td></tr>
						<tr class="linhaImpar"><td align="left">Aprovado em dependência</td><td align="right">${ totalAprovDepend }</td></tr>
					</table>
				</td>
			</tr>
						
		</tbody>
		<tfoot>
			<tr>
				<td colspan="6" align="center">
			   		<h:commandButton value="<< Voltar" action="#{ processamentoMatriculaDiscenteSerie.formProcessamento}" id="btnVoltar"/>
					<h:commandButton value="Cancelar" id="btnCancelar" action="#{ processamentoMatriculaDiscenteSerie.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br>
</h:form>
</f:view>	
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
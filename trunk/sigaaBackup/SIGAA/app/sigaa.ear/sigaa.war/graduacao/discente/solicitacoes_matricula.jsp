<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<f:view>
	<h2><ufrn:subSistema/> > Consultar Solicitações de Matrículas</h2>
	<c:set var="discente" value="#{consultaSolicitacoes.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<center>
	<h:form>
		<input type="hidden" value="${consultaSolicitacoes.discente.id}" name="id" />
			<h:graphicImage url="/img/report.png"></h:graphicImage>
		<h:commandLink value="Clique aqui " action="#{historico.selecionaDiscenteForm}">
			<f:param value="#{consultaSolicitacoes.discente.id}" name="id"/>
		</h:commandLink>
		para Visualizar Histórico do Aluno
	</h:form>
	<h:form>
		<input type="hidden" value="${consultaSolicitacoes.discente.curriculo.id}" name="id" />
		<h:graphicImage url="/img/graduacao/matriculas/turmas_curriculo.png"></h:graphicImage>
		<c:if test="${consultaSolicitacoes.discente.graduacao}">
		<h:commandLink action="#{curriculo.gerarRelatorioCurriculo}" value="Clique Aqui" target="_blank" />
		</c:if>
		<c:if test="${consultaSolicitacoes.discente.tecnico}">
		<html:link target="_blank" action="/ensino/tecnico/estruturaCurricular/wizard?dispatch=visualizar&page=null&id=${consultaSolicitacoes.discente.estruturaCurricularTecnica.id}">
		Clique Aqui
		</html:link>
		</c:if>
		para ver as disciplinas da estrutura curricular do discente.
	</h:form>
	</center><br>
		<h:form>
		<table class="listagem" style="width: 100%">
		<caption>Matrículas Orientadas</caption>
		<thead style="font-size: xx-small;">
			<tr>
				<td width="1%"></td>
				<td width="4%"></td>
				<td>Componente Curricular</td>
				<td width="7%">Situação</td>
				<td width="15%">Submetida em</td>
				<td width="15%">Analisada em</td>
				<td width="8%">Resultado do Processamento</td>
			</tr>
		</thead>
		<tbody>
			<c:set var="exibeBotaoAnular" value="false" />
			<c:forEach items="#{consultaSolicitacoes.solicitacoes}" var="solicitacao" varStatus="status">
				<c:set value="#{solicitacao.turma}" var="turma" />
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td>
						<c:if test="${consultaSolicitacoes.passivelAnulacao and !solicitacao.processada}">
							<input type="checkbox" onclick="marcarAnulado(this)" name="anulados" value="${solicitacao.id}" id="anul_${solicitacao.id}" class="noborder" ${solicitacao.anulado ? 'checked=checked' : '' }>
							<c:set var="exibeBotaoAnular" value="true" />
						</c:if>
					</td>
					<td align="center">
						<a href="#" onclick="PainelTurma.show(${turma.id})" title="Ver Detalhes dessa turma">
						T${turma.codigo}
						</a>
					</td>
					<td>
						<a href="#" onclick="PainelComponente.show(${turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
						${turma.disciplina.detalhes.codigo}
						</a> - ${turma.disciplina.nome}
					</td>
					<td>${solicitacao.statusDescricao}</td>
					<td> <ufrn:format type="dataHora" valor="${solicitacao.dataCadastro}" /> </td>
					<td><ufrn:format type="dataHora" valor="${solicitacao.dataAlteracao}" /></td>
					<td>${solicitacao.resultadoProcessamento}</td>
				</tr>
				<c:if test="${not empty solicitacao.observacao }">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td colspan="7" style="font-weight: bold">Observações (${solicitacao.registroAlteracao.usuario.login}):</td>
					</tr>
					<tr>
					<td colspan="7">${solicitacao.observacao }</td>
					</tr>
				</c:if>
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="font-size: xx-small">
					<td colspan="7">
					<c:if test="${consultaSolicitacoes.passivelAnulacao}">
					<table align="right" id="motivo_${solicitacao.id}" style="${!solicitacao.anulado ? 'display:none' : ''}" width="100%">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="font-size: xx-small">
						<td valign="top"><b>Motivo da Anulação:</b>
						<c:if test="${not empty solicitacao.registroAnulacao}">
						<br>(${solicitacao.registroAnulacao.usuario.login })
						</c:if>
						</td>
						<td align="left">
						<textarea rows="2" cols="120" name="motivo_${solicitacao.id}" style="width: 90%">${solicitacao.observacaoAnulacao}</textarea>
						</td>
						</tr>
					</table>
					</c:if>
					<c:if test="${!consultaSolicitacoes.passivelAnulacao  and solicitacao.anulado}">
					Anulado. Motivo: ${solicitacao.observacaoAnulacao}
					</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<table class="formulario" style="width: 100%">
		<tfoot>
			<tr><td>
				<c:if test="${exibeBotaoAnular }">
					<h:commandButton value="Anular Solicitações Marcadas" action="#{consultaSolicitacoes.anularMarcadas}" rendered="#{consultaSolicitacoes.passivelAnulacao}" id="anularSolicitacoesMarcadas"/>
				</c:if>
				<h:commandButton value="Selecionar Outro Discente" action="#{consultaSolicitacoes.iniciar}" id="outroDiscente"/>
				<h:commandButton value="Cancelar" action="#{consultaSolicitacoes.cancelar}" id="cancelarOp"/>
			</td></tr>
		</tfoot>
	</table>

		</h:form>
</f:view>

<script>
	function marcarAnulado(check){
		var id = check.id.substring(5);
		var motivo = getEl( 'motivo_' + id ).dom;
		motivo.style.display=(check.checked ? 'block' : 'none');
	}
</script>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
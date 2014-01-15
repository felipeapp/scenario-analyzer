<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>

<style>
 p.vazio { color: #922; margin: 10px; text-align: center; font-weight: bold; }
 span.statusProcessamento { color: #777; font-weight: bold; font-size: 0.9em; }
td.subFormulario { line-height: 1.5em; text-align: center; background: #EEE;}
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Plano de Matrícula ${planoMatriculaBean.calendario.anoPeriodo }</h2>

	<c:set value="#{planoMatriculaBean.discente }" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>

	<table class="listagem" style="width: 100%">
		<caption>Plano de Matrícula</caption>
		<thead style="font-size: xx-small;">
			<tr>
				<th width="8%">Turma</th>
				<th>Componente Curricular</th>
				<th width="10%" nowrap="nowrap">Cadastrada em</th>
				<th width="20%" style="text-align: center;">Situação</th>
				<th width="10%" nowrap="nowrap">Analisada em</th>
				<th>Analisada por</th>
			</tr>
		</thead>
		<tbody>

		<%-- Solitações de Matrícula  --%>
		<c:if test="${not empty planoMatriculaBean.solicitacoesMatricula}">
		<tr>
			<td colspan="6" class="subFormulario"> Solicitações de Matrícula </td>
		</tr>
		<c:forEach items="${planoMatriculaBean.solicitacoesMatricula}" var="solicitacao" varStatus="status">
			<c:set var="analisePositiva" value="${solicitacao.atendida or solicitacao.vista}" />				
			<c:set var="analiseNegativa" value="${solicitacao.negada or solicitacao.inDeferida}" />				

			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td align="center">
					<c:if test="${not empty solicitacao.turma}">
					<a href="javascript:void(0);" onclick="PainelTurma.show(${solicitacao.turma.id})" title="Ver Detalhes dessa turma">
						Turma ${solicitacao.turma.codigo}
					</a>
					</c:if>
				</td>
				<td>	
					<b>
					<a href="javascript:void(0);" onclick="PainelComponente.show(${solicitacao.turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
					${solicitacao.componente.codigo}
					</a> - ${solicitacao.componente.nome}
					</b>
				</td>
				<td><ufrn:format type="dataHora" valor="${solicitacao.dataCadastro}" /></td>
				<td style="text-align: center;"> 
					<span class="statusProcessamento"> 
						${solicitacao.statusDescricao} <br />
					</span> 
					<c:if test="${!solicitacao.discente.stricto or !solicitacao.negada}">
							${solicitacao.processamentoStatus} 
					</c:if>
				</td>
				<td><ufrn:format type="dataHora" valor="${solicitacao.dataAlteracao}" /></td>
				<td>${solicitacao.registroAlteracao.usuario.pessoa.nome}</td>
			</tr>
			<%-- 
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size:0.8em;">
				<td> </td>
				<td colspan="3">
					Docente(s): ${solicitacao.turma.docentesNomes }
				</td>
			</tr>
			--%>
			<c:if test="${not empty solicitacao.observacao }">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td> </td>
					<td colspan="5"><i>Observações:</i> ${solicitacao.observacao }</td>
				</tr>
				<c:if test="${solicitacao.discente.tecnico or solicitacao.discente.stricto}">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td> </td>
					<td colspan="5"> <i>Analisada por ${solicitacao.registroAlteracao.usuario.nome}</i></td>
				</tr>
				</c:if>
			</c:if>
		</c:forEach>
		</c:if>
		
		<%-- Solicitações de Matrícula na Rematrícula  --%>
		<c:if test="${not empty planoMatriculaBean.solicitacoesMatriculaRematricula}">
		<tr>
			<td colspan="6" class="subFormulario"> Solicitações de Matrícula na Rematrícula </td>
		</tr>
		<c:forEach items="${planoMatriculaBean.solicitacoesMatriculaRematricula}" var="solicitacao" varStatus="status">
			<c:set var="analisePositiva" value="${solicitacao.atendida or solicitacao.vista}" />				
			<c:set var="analiseNegativa" value="${solicitacao.negada or solicitacao.inDeferida}" />				

			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td align="center">
					<c:if test="${not empty solicitacao.turma}">
					<a href="javascript:void(0);" onclick="PainelTurma.show(${solicitacao.turma.id})" title="Ver Detalhes dessa turma">
						Turma ${solicitacao.turma.codigo}
					</a>
					</c:if>
				</td>
				<td>	
					<b>
					<a href="javascript:void(0);" onclick="PainelComponente.show(${solicitacao.turma.disciplina.id})" title="Ver Detalhes do Componente Curricular">
					${solicitacao.componente.codigo}
					</a> - ${solicitacao.componente.nome}
					</b>
				</td>
				<td><ufrn:format type="dataHora" valor="${solicitacao.dataCadastro}" /></td>
				<td style="text-align: center;"> 
					<span class="statusProcessamento"> 
						${solicitacao.statusDescricao} <br />
					</span> 
					<c:if test="${!solicitacao.discente.stricto or !solicitacao.negada}">
							${solicitacao.processamentoStatus} 
					</c:if>
				</td>
				<td><ufrn:format type="dataHora" valor="${solicitacao.dataAlteracao}" /></td>
				<td>${solicitacao.registroAlteracao.usuario.pessoa.nome}</td>
			</tr>
			<%-- 
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size:0.8em;">
				<td> </td>
				<td colspan="3">
					Docente(s): ${solicitacao.turma.docentesNomes }
				</td>
			</tr>
			--%>
			<c:if test="${not empty solicitacao.observacao }">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td> </td>
					<td colspan="5"><i>Observações:</i> ${solicitacao.observacao }</td>
				</tr>
				<c:if test="${solicitacao.discente.tecnico or solicitacao.discente.stricto}">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td> </td>
					<td colspan="5"> <i>Analisada por ${solicitacao.registroAlteracao.usuario.nome}</i></td>
				</tr>
				</c:if>
			</c:if>
		</c:forEach>
		</c:if>
	
		<%-- Solitações de Ensino Individual  --%>
		<c:if test="${not empty planoMatriculaBean.solicitacoesEnsinoIndividual}">
			<tr>
				<td colspan="6" class="subFormulario"> Solicitações de Ensino Individual </td>
			</tr>
			<c:forEach items="${planoMatriculaBean.solicitacoesEnsinoIndividual}" var="solicitacao" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td></td>
					<td>	
						<b>
						<a href="javascript:void(0);" onclick="PainelComponente.show(${solicitacao.componente.id})" title="Ver Detalhes do Componente Curricular">
						${solicitacao.componente.codigo}
						</a> - ${solicitacao.componente.nome}
						</b>
					</td>
					<td><ufrn:format type="dataHora" valor="${solicitacao.dataSolicitacao}" /></td>
					<td style="text-transform: uppercase; text-align: center"> 
						${solicitacao.situacaoString} 
					</td>
					<td><ufrn:format type="dataHora" valor="${solicitacao.dataAtendimento}" /></td>
					<td>${solicitacao.registroEntradaAtendente.usuario.pessoa.nome}</td>
				</tr>
			</c:forEach>
		</c:if>

		<%-- Matrículas em Turmas de Férias  --%>
		<c:if test="${not empty planoMatriculaBean.matriculasFerias}">
		<tr>
			<td colspan="6" class="subFormulario"> Matrículas em Turmas de Férias </td>
		</tr>
		<c:forEach items="${planoMatriculaBean.matriculasFerias}" var="matricula" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td align="center">
					<c:if test="${not empty matricula.turma}">
					<a href="javascript:void(0);" onclick="PainelTurma.show(${matricula.turma.id})" title="Ver Detalhes dessa turma">
						Turma ${matricula.turma.codigo}
					</a>
					</c:if>
				</td>
				<td>	
					<b>
					<a href="javascript:void(0);" onclick="PainelComponente.show(${matricula.componente.id})" title="Ver Detalhes do Componente Curricular">
					${matricula.componente.codigo}
					</a> - ${matricula.componente.nome} 
					(${matricula.anoPeriodo})
					</b>
				</td>
				<td> </td>
				<td style="text-transform: uppercase; text-align: center"> 
					${matricula.situacaoMatricula.descricao} 
				</td>
				<td></td>
				<td></td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size:0.8em;">
				<td> </td>
				<td colspan="3">
					Docente(s): ${matricula.turma.docentesNomes}
				</td>
			</tr>
		</c:forEach>
		</c:if>

		<%-- Matrículas sem Solicitação pelo Discente  --%>
		<c:if test="${not empty planoMatriculaBean.matriculasSemSolicitacao}">
		<tr>
			<td colspan="6" class="subFormulario"> Matrículas sem Solicitação pelo Discente </td>
		</tr>
		<c:forEach items="${planoMatriculaBean.matriculasSemSolicitacao}" var="matricula" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td align="center">
					<c:if test="${not empty matricula.turma}">
					<a href="javascript:void(0);" onclick="PainelTurma.show(${matricula.turma.id})" title="Ver Detalhes dessa turma">
						Turma ${matricula.turma.codigo}
					</a>
					</c:if>
				</td>
				<td>	
					<b>
					<a href="javascript:void(0);" onclick="PainelComponente.show(${matricula.componente.id})" title="Ver Detalhes do Componente Curricular">
					${matricula.componente.codigo}
					</a> - ${matricula.componente.nome} 
					</b>
				</td>
				<td> 
					<ufrn:format type="dataHora" valor="${matricula.dataCadastro}" /> 
				</td>
				<td style="text-transform: uppercase; text-align: center"> 
					${matricula.situacaoMatricula.descricao} 
				</td>
				<td></td>
				<td></td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size:0.8em;">
				<td> </td>
				<td>
					Docente(s): ${matricula.turma.docentesNomes}
				</td>
				<td style="color: #555;"> (Usuário: <b>${matricula.registroEntrada.usuario.login}</b>)</td>
				<td> </td>
			</tr>
		</c:forEach>
		</c:if>
	</tbody>
	</table>
	<br>
	<h:form>
		<center>
		<h:commandButton value="Selecionar Outro Discente" action="#{planoMatriculaBean.buscarDiscente}"
			rendered="#{!acesso.discente}" id="outroDiscente"/>
		<h:commandButton value="Cancelar" action="#{planoMatriculaBean.cancelar}" id="cancelarOp" onclick="#{confirm}"/>
		</center>
	</h:form>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

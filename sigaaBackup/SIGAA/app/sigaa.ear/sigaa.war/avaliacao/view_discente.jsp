
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.arq.erros.NegocioException"%><script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>
<script type="text/javascript">
function submitTheForm(element) {
   var myForm = document.getElementById('form');
   element.disable = true;
   myForm.submit();
}
</script>

<f:view>

<h:form id="form">

<input type="hidden" name="aba" id="aba"/>
<h2><ufrn:subSistema /> &gt; Questionário da Avaliação da Docência pelo Aluno</h2>

<c:set var="turmasComUmDocente" value="${ avaliacaoInstitucionalAnterior.docenteTurmasDiscente }"/>
<c:set var="turmasMaisDeUmDocente" value="${ avaliacaoInstitucionalAnterior.turmasDiscenteComMaisDeUmDocente }"/>

<c:set var="trancamentos" value="${ avaliacaoInstitucionalAnterior.trancamentosDiscente }"/>
<c:set var="aval" value="${ avaliacaoInstitucionalAnterior.obj }"/>
<jsp:useBean id="perguntaAnterior" class="br.ufrn.sigaa.avaliacao.dominio.Pergunta"/>

<div class="descricaoOperacao">
<p>Você está visualizando a avaliação do ano-período ${avaliacaoInstitucionalAnterior.ano }.${avaliacaoInstitucionalAnterior.periodo } (anterior), não sendo possível alterá-la.</p>
</div>

<%-- GRUPOS DE PERGUNTAS --%>
<c:forEach var="grupo" items="#{ avaliacaoInstitucionalAnterior.gruposDiscente }" varStatus="grupoLoop">
<div id="dimensoes-avaliacao-${ grupo.id }" class="reduzido">
	<div id="elgen-8" class="ytab-wrap">
		<div class="ytab-strip-wrap">
		<table id="elgen-10" class="ytab-strip" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr id="elgen-9" class="">
					 <td id="elgen-15" class="on" style="width: 150px;" align="center">
						<a id="elgen-12" class="ytab-right" href="#"> 
						<span class="ytab-left"> 
						<em id="elgen-13" class="ytab-inner">
						<span id="elgen-14" class="ytab-text" title="${ grupo.titulo }" unselectable="on">${ grupo.titulo }</span> 
						</em></span>
						</a>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
	</div>

	<div id="dim${ grupo.id }" class="aba">
	<h3>${ grupo.descricao }</h3>

	<%-- O GRUPO AVALIA CADA TURMA --%>
	<c:if test="${ grupo.avaliaTurmas }">
		<%-- TURMAS COM APENAS UM DOCENTE  --%>
		<c:if test="${ not empty turmasComUmDocente }">

			<%-- CABECALHO COM CODIGOS DOS COMPONENTES --%>		
			<table width="100%" class="turma"><tr><td width="425">&nbsp;</td>
			<c:forEach var="t" items="${ turmasComUmDocente }">
			<td width="50" align="center"><a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="${ t.docenteDescricao }">${ t.disciplina.codigo }</a></td>
			</c:forEach><td></td></tr>
			</table>
		
			<%-- PERGUNTAS --%>
			<c:forEach items="#{ grupo.perguntas }" var="p" varStatus="loop">
				<c:if test="${ perguntaAnterior.avaliarTurmas && !p.avaliarTurmas && empty turmasMaisDeUmDocente }">
				<br/>
				<h4>Perguntas Gerais</h4>
				</c:if>
			
				<table width="100%" ${ sf:contains(avaliacaoInstitucionalAnterior.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
				
				<c:if test="${ p.nota }">
					<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:if test="${ p.avaliarTurmas }">
							<c:forEach var="t" items="${ turmasComUmDocente }" varStatus="irep">
								<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="true"/></td>
							</c:forEach>
						</c:if>
						<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
							<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }" readOnly="true"/></td>
						</c:if>
						<td></td>
					</tr>
				</c:if>	
				
				<c:if test="${ p.escolhaUnica and empty turmasMaisDeUmDocente }">
					<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
					<c:forEach var="alt" items="${ p.alternativas }">
						<tr><td width="20"></td>
						<td width="10">
							<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/></td>
							<td>${ alt.descricao }
							<c:if test="${ alt.permiteCitacao }">
								<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/>
							</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:if>
				
				<c:if test="${ p.multiplaEscolha and empty turmasMaisDeUmDocente }">
					<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
					<c:forEach var="alt" items="${ p.alternativas }">
						<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/></td>
						<td>${ alt.descricao }
						<c:if test="${ alt.permiteCitacao }">
							<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/>
						</c:if>
						</td></tr>
					</c:forEach>
				</c:if>
				
				<c:if test="${ p.simNao }">
					<c:if test="${ p.avaliarTurmas }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:forEach var="t" items="${ turmasComUmDocente }">
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="true"/></td>
						</c:forEach><td></td>
						</tr>
					</c:if>
					<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ aval }" readOnly="true"/></td><td></td>
						</tr>
					</c:if>
				</c:if>
				
				</table>
				<c:set value="${ p }" var="perguntaAnterior"/>
			</c:forEach>
		
		</c:if>

		<%-- TURMAS COM MAIS DE UM DOCENTE --%>
		<c:if test="${ not empty turmasMaisDeUmDocente }">
		<br/>
			<h4>As turmas abaixo possuem mais de um professor, por isso é necessário avaliar cada um deles</h4><br/>
		
			<%-- PARA CADA TURMA, LISTAR OS DOCENTES --%>
			<c:forEach var="t" items="${ turmasMaisDeUmDocente }" varStatus="countTurmas">
				<table width="100%" ${ sf:contains(avaliacaoInstitucionalAnterior.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
				<tr>
				<td width="425"><a href="javascript:void(0);" onclick="PainelTurma.show(${t.id})" title="Ver Detalhes dessa turma">${ t.disciplina.codigoNome }</a></td>
				<c:forEach var="dt" items="${ t.docentesTurmas }" varStatus="irep">
					<td width="50" align="center"><acronym title="${ dt.docenteDescricao }">${ dt.primeiroNomeDocente }</acronym></td>
				</c:forEach><td></td>
				</tr>
				</table> 
			
				<%-- PERGUNTAS --%>
				<c:forEach items="#{ grupo.perguntas }" var="p" varStatus="loop">
					<c:if test="${ perguntaAnterior.avaliarTurmas && !p.avaliarTurmas && countTurmas.last}">
					<br/>
					<h4>Perguntas Gerais</h4>
					</c:if>
				
					<table width="100%" ${ sf:contains(avaliacaoInstitucionalAnterior.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
					
					<c:if test="${ p.nota }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
							<c:if test="${ p.avaliarTurmas }">
								<c:forEach var="dt" items="${ t.docentesTurmas }" varStatus="irep">
									<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ dt.id }" aval="${ aval }" readOnly="true"/></td>
								</c:forEach>
							</c:if>
							<c:if test="${ countTurmas.last and not p.avaliarTurmas }">
								<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }" readOnly="true"/></td>
							</c:if>
							<td></td>
						</tr>
					</c:if>	
					
					<c:if test="${ p.escolhaUnica and countTurmas.last }">
						<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
						<c:forEach var="alt" items="${ p.alternativas }">
							<tr><td width="20"></td>
							<td width="10">
								<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/></td>
								<td>${ alt.descricao }
								<c:if test="${ alt.permiteCitacao }">
									<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/>
								</c:if>
								</td>
							</tr>
						</c:forEach>
					</c:if>
					
					<c:if test="${ p.multiplaEscolha and countTurmas.last }">
						<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
						<c:forEach var="alt" items="${ p.alternativas }">
							<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/></td>
							<td>${ alt.descricao }
							<c:if test="${ alt.permiteCitacao }">
								<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/>
							</c:if>
							</td></tr>
						</c:forEach>
					</c:if>
					
					<c:if test="${ p.simNao }">
						<c:if test="${ p.avaliarTurmas }">
							<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
							<c:forEach var="dt" items="${ t.docentesTurmas }">
							<td width="50" align="center"><aval:simNao pergunta="${ p.id }" tid="${ dt.id }" aval="${ aval }" readOnly="true"/></td>
							</c:forEach><td></td>
							</tr>
						</c:if>
						<c:if test="${ countTurmas.last and not p.avaliarTurmas}">
							<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
							<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ aval }" readOnly="true"/></td><td></td>
							</tr>
						</c:if>
					</c:if>
					
					</table>
					<c:set value="${ p }" var="perguntaAnterior"/>
				</c:forEach>
				<br/>
			</c:forEach> 
		
		</c:if>
	</c:if>
	<%-- O GRUPO NAO AVALIA TURMAS --%>
	<c:if test="${ not grupo.avaliaTurmas }">
		
		<%-- PERGUNTAS --%>
		<c:forEach items="#{ grupo.perguntas }" var="p" varStatus="loop">				
			<table width="100%" ${ sf:contains(avaliacaoInstitucionalAnterior.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
				
			<c:if test="${ p.nota }">
				<tr><td>${ loop.index + 1 }. ${ p.descricao }</td>
					<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }" readOnly="true"/></td>
				</tr>
			</c:if>	
				
			<c:if test="${ p.escolhaUnica }">
				<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
				<c:forEach var="alt" items="${ p.alternativas }">
					<tr><td width="20"></td>
					<td width="10">
						<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/></td>
						<td>${ alt.descricao }
						<c:if test="${ alt.permiteCitacao }">
							<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/>
						</c:if>
						</td>
					</tr>
				</c:forEach>
			</c:if>
				
			<c:if test="${ p.multiplaEscolha }">
				<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
				<c:forEach var="alt" items="${ p.alternativas }">
					<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/></td>
					<td>${ alt.descricao }
					<c:if test="${ alt.permiteCitacao }">
						<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" readOnly="true"/>
					</c:if>
					</td></tr>
				</c:forEach>
			</c:if>
				
			<c:if test="${ p.simNao }">
			<tr>
				<td>${ loop.index + 1 }. ${ p.descricao }</td>
				<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ aval }" readOnly="true"/></td>
			</tr>
			</c:if>
			
			</table>
		</c:forEach>
		
	</c:if>
<c:if test="${ !grupoLoop.last }">	
</div>
</div>
</c:if>
<br/>
</c:forEach>


<%-- TRANCAMENTOS --%>
Realizou trancamento de disciplina(s) neste período letivo? <strong>${ empty trancamentos ? 'Não' : 'Sim' }</strong> <br/>
<c:if test="${ not empty trancamentos }">
Em caso positivo, efetuou trancamento em quantas disciplinas? <strong>${ fn:length(trancamentos) }</strong> <br/>
Relacione a(s) razão(ões) para o(s) trancamento(s):<br/><br/>
<table width="100%">	
	<c:forEach var="t" items="${ trancamentos }">
		<tr><td align="center">${ t.disciplina.codigoNome }</td></tr>
		<tr><td width="100" align="center"><aval:trancamento aval="${ aval }" tid="${ t.id }" readOnly="true"/></td></tr>
	</c:forEach>
</table>
</c:if>
</div>

<br/>

<div id="dimensao-comentario" class="reduzido">
	<div id="elgen-8" class="ytab-wrap">
		<div class="ytab-strip-wrap">
		<table id="elgen-10" class="ytab-strip" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr id="elgen-9" class="">
					<td id="elgen-15" class="on" style="width: 150px;" align="center">
						<a id="elgen-12" class="ytab-right" href="#"> 
						<span class="ytab-left"> 
						<em id="elgen-13" class="ytab-inner">
						<span id="elgen-14" class="ytab-text" title="Comentários Adicionais" unselectable="on">Comentários Adicionais</span> 
						</em></span>
						</a>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
	</div>
	
<div id="comentario" class="aba">
<h3>ESPAÇO DESTINADO PARA COMENTÁRIOS OPCIONAIS</h3>
<center>

<h5>Deseja comentar sobre quais disciplinas/professores?</h5>

<table width="100%">
<c:forEach var="dt" items="#{ avaliacaoInstitucionalAnterior.docenteTurmasDiscente }">
<tr><td>
<aval:observacaoSimples aval="${ aval }" dtId="${ dt.id }" codigo="${ dt.turma.disciplina.codigo } - ${ dt.docenteNome }" readOnly="true"/>
</td></tr>
</c:forEach>

<c:forEach var="t" items="#{ avaliacaoInstitucionalAnterior.turmasDiscenteComMaisDeUmDocente }">
<tr><td>
<aval:observacaoMultiplo aval="${ aval }" t="${ t }" codigo="${ t.disciplina.codigo }" readOnly="true"/>
</td></tr>
</c:forEach>

</table>


<h5>Comentários gerais</h5>
<h:inputTextarea value="#{ aval.observacoes }" rows="8" cols="105" disabled="true"/>
</center>

</div>
</div>

</div>

</h:form>
<br/>
<center><a href="javascript:history.go(-1)"> << Voltar</a></center>
</f:view>

<script type="text/javascript" src="${ctx}/javascript/avaliacao.js"></script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.arq.erros.NegocioException"%><script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>

<script type="text/javascript">
function limitText(limitField, limitCount, limitNum) {
	if (limitField.value.length > limitNum) {
		limitField.value = limitField.value.substring(0, limitNum);
	} else {
		$(limitCount).value = limitNum - limitField.value.length;
	}
}
</script>

<f:view>

<a4j:region rendered="#{ acesso.acessibilidade }">
	<script type="text/javascript" src="/shared/javascript/jquery/jquery-1.4.4.min.js"></script>
	<script src="${ctx}/javascript/jquery.acessibilidade.js" type="text/javascript" ></script>
</a4j:region>

<h:form id="form">

<c:set var="tamanhoColunaPergunta" value="425"/>

<h:messages showDetail="true"></h:messages>
${ avaliacaoInstitucional.verificaAcessoDiscente }

<input type="hidden" name="aba" id="aba"/>
<h2><a href="${ctx}/verPortalDiscente.do">Portal Discente</a> &gt; Questionário da Avaliação da Docência pelo Aluno</h2>

<c:set var="turmasComUmDocente" value="${ avaliacaoInstitucional.docenteTurmasDiscente }"/>
<c:set var="turmasMaisDeUmDocente" value="${ avaliacaoInstitucional.turmasDiscenteComMaisDeUmDocente }"/>

<c:set var="trancamentos" value="${ avaliacaoInstitucional.trancamentosDiscente }"/>
<c:set var="aval" value="${ avaliacaoInstitucional.obj }"/>
<jsp:useBean id="perguntaAnterior" class="br.ufrn.sigaa.avaliacao.dominio.Pergunta"/>

<div class="descricaoOperacao">
<c:if test="${not empty avaliacaoInstitucional.formulario.instrucoesGerais }">
	${avaliacaoInstitucional.formulario.instrucoesGerais}
</c:if>
<c:if test="${empty avaliacaoInstitucional.formulario.instrucoesGerais }">
	<h4> Caro aluno, </h4>
	<p>Esta avaliação é parte de um processo mais amplo de avaliação do Ensino Superior, determinado pela Lei
	Federal n&ordm; 10.861/04 e pela ${ avaliacaoInstitucional.resolucao }, executada pela ${ configSistema['siglaInstituicao'] } e tem em vista a melhoria das
	condições de ensino e de aprendizagem na graduação. O resultado será discutido pela comunidade acadêmica da
	${ configSistema['siglaInstituicao'] }. Suas respostas são de fundamental importância para a avaliação. A ${ configSistema['siglaInstituicao'] } agradece a sua participação.</p>
	<h5>Instruções para preenchimento:</h5>
	<ol>
		<li> Para as perguntas que requerem atribuição de notas, utilize a escala a seguir como referência: <br />
			<strong>0, 1, 2, 3</strong> - Deficiente; <strong>4, 5, 6</strong> - Regular; <strong>7, 8</strong> - Bom; <strong>9, 10</strong> - Excelente; <strong>N/A</strong> - Sem condições de avaliar ou não se aplica.
		</li>
		<li>
			Para ver detalhes da turma que você está avaliando, basta clicar no código ou nome da disciplina. 
		</li>
		<li>
			A qualquer momento você pode gravar as informações digitadas na avaliação clicando no botão <strong>Salvar</strong>. Se você desejar, poderá
			salvar os dados e continuar a avaliação em um outro momento.
		</li>
		<li>
			A sua avaliação só será realmente enviada no momento em que você clicar no botão <strong>Finalizar</strong>.
		</li>
	</ol>
</c:if>
</div>

<a4j:poll action="#{ avaliacaoInstitucional.salvar }" interval="300000"/>
<a4j:status startText="Salvando..."/>

<%-- GRUPOS DE PERGUNTAS --%>
<c:forEach var="grupo" items="#{ avaliacaoInstitucional.gruposDiscente }" varStatus="grupoLoop">
<div id="dimensoes-avaliacao-${ grupo.id }" class="reduzido">
	<div id="elgen-8" class="ytab-wrap">
		<div class="ytab-strip-wrap">
		<table id="elgen-10" class="ytab-strip" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr id="elgen-9" class="">
					 <td id="elgen-15" class="on" style="width: 165px;" align="center">
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
			<table width="100%" class="turma"><tr><td width="${tamanhoColunaPergunta}">&nbsp;</td>
			<c:forEach var="t" items="${ turmasComUmDocente }">
			<td width="50" align="center">
				<a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="${ t.docenteDescricao }">
					${ t.disciplina.codigo }
					<c:if test="${t.disciplina.excluirAvaliacaoInstitucional}">(NA)</c:if> 
				</a>
			</td>
			</c:forEach><td></td></tr>
			</table>
		
			<%-- PERGUNTAS --%>
			<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">
				<c:if test="${ perguntaAnterior.avaliarTurmas && !p.avaliarTurmas && empty turmasMaisDeUmDocente }">
				<br/>
				<h4>Perguntas Gerais</h4>
				</c:if>
			
				<table width="100%" ${ sf:contains(avaliacaoInstitucional.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
				
				<c:if test="${ p.nota }">
					<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:if test="${ p.avaliarTurmas }">
							<c:forEach var="t" items="${ turmasComUmDocente }" varStatus="irep">
								<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="${t.disciplina.excluirAvaliacaoInstitucional}"/></td>
							</c:forEach>
						</c:if>
						<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
							<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"  readOnly="${t.disciplina.excluirAvaliacaoInstitucional}"/></td>
						</c:if>
						<td></td>
					</tr>
				</c:if>	
				
				<c:if test="${ p.escolhaUnica and empty turmasMaisDeUmDocente }">
					<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
					<c:forEach var="alt" items="${ p.alternativas }">
						<tr><td width="20"></td>
						<td width="10">
							<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
							<td>${ alt.descricao }
							<c:if test="${ alt.permiteCitacao }">
								<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
							</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:if>
				
				<c:if test="${ p.multiplaEscolha and empty turmasMaisDeUmDocente }">
					<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
					<c:forEach var="alt" items="${ p.alternativas }">
						<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
						<td>${ alt.descricao }
						<c:if test="${ alt.permiteCitacao }">
							<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
						</c:if>
						</td></tr>
					</c:forEach>
				</c:if>
				
				<c:if test="${ p.simNao }">
					<c:if test="${ p.avaliarTurmas }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:forEach var="t" items="${ turmasComUmDocente }">
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="${t.disciplina.excluirAvaliacaoInstitucional}"/></td>
						</c:forEach><td></td>
						</tr>
					</c:if>
					<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ aval }"  readOnly="${t.disciplina.excluirAvaliacaoInstitucional}"/></td><td></td>
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
				<c:set var="perguntaTurma" value="P${p.id}T${t.id}"/>
				<table width="100%" ${ sf:contains(avaliacaoInstitucional.perguntaTurmaNaoRespondidas, perguntaTurma) ? 'class="nao-respondida"' : '' }>
				<tr>
				<td width="${tamanhoColunaPergunta}"><a href="javascript:void(0);" onclick="PainelTurma.show(${t.id})" title="Ver Detalhes dessa turma">${ t.disciplina.codigoNome }</a></td>
				<c:forEach var="dt" items="${ t.docentesTurmas }" varStatus="irep">
					<td width="50" align="center"><acronym title="${ dt.docenteDescricao }">${ dt.primeiroNomeDocente }</acronym></td>
				</c:forEach><td></td>
				</tr>
				</table> 
			
				<%-- PERGUNTAS --%>
				<c:if test="${t.disciplina.excluirAvaliacaoInstitucional}"><h4>Esta disciplina não será avaliada</h4>
				</c:if>
				<c:if test="${not t.disciplina.excluirAvaliacaoInstitucional}">
					<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">
						<c:if test="${ perguntaAnterior.avaliarTurmas && !p.avaliarTurmas && countTurmas.last}">
						<br/>
						<h4>Perguntas Gerais</h4>
						</c:if>
					
						<c:set var="perguntaNaoRespondida" value="false" />
						<c:choose>
							<c:when test="${p.avaliarTurmas}">
								<c:set var="perguntaTurma" value="P${p.id}T${t.id}"/>
								<c:set var="perguntaNaoRespondida" value="${sf:contains(avaliacaoInstitucional.perguntaTurmaNaoRespondidas, perguntaTurma)}" />
							</c:when>
							<c:when test="${ not p.avaliarTurmas }">
								<c:set var="perguntaNaoRespondida" value="${sf:contains(avaliacaoInstitucional.perguntasNaoRespondidas, p.id)}" />
							</c:when>
						</c:choose>
						
						<table width="100%" ${ perguntaNaoRespondida ? 'class="nao-respondida"' : '' }>
						
						<c:if test="${ p.nota }">
							<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
								<c:if test="${ p.avaliarTurmas }">
									<c:forEach var="dt" items="${ t.docentesTurmas }" varStatus="irep">
										<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ dt.id }" aval="${ aval }"/></td>
									</c:forEach>
								</c:if>
								<c:if test="${ countTurmas.last and not p.avaliarTurmas }">
									<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"/></td>
								</c:if>
								<td></td>
							</tr>
						</c:if>	
						
						<c:if test="${ p.escolhaUnica and countTurmas.last }">
							<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
							<c:forEach var="alt" items="${ p.alternativas }">
								<tr><td width="20"></td>
								<td width="10">
									<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
									<td>${ alt.descricao }
									<c:if test="${ alt.permiteCitacao }">
										<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
									</c:if>
									</td>
								</tr>
							</c:forEach>
						</c:if>
						
						<c:if test="${ p.multiplaEscolha and countTurmas.last }">
							<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
							<c:forEach var="alt" items="${ p.alternativas }">
								<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
								<td>${ alt.descricao }
								<c:if test="${ alt.permiteCitacao }">
									<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
								</c:if>
								</td></tr>
							</c:forEach>
						</c:if>
						
						<c:if test="${ p.simNao }">
							<c:if test="${ p.avaliarTurmas }">
								<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
								<c:forEach var="dt" items="${ t.docentesTurmas }">
								<td width="50" align="center"><aval:simNao pergunta="${ p.id }" tid="${ dt.id }" aval="${ aval }"/></td>
								</c:forEach><td></td>
								</tr>
							</c:if>
							<c:if test="${ countTurmas.last and not p.avaliarTurmas}">
								<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
								<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ aval }"/></td><td></td>
								</tr>
							</c:if>
						</c:if>
						
						</table>
						<c:set value="${ p }" var="perguntaAnterior"/>
					</c:forEach>
				</c:if>
				<br/>
			</c:forEach> 
		
		</c:if>
	</c:if>
	<%-- O GRUPO NAO AVALIA TURMAS --%>
	<c:if test="${ not grupo.avaliaTurmas }">
		
		<%-- PERGUNTAS --%>
		<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">				
			<table width="100%" ${ sf:contains(avaliacaoInstitucional.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
				
			<c:if test="${ p.nota }">
				<tr><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
					<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"/></td>
					<td></td>
				</tr>
			</c:if>	
				
			<c:if test="${ p.escolhaUnica }">
				<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
				<c:forEach var="alt" items="${ p.alternativas }">
					<tr><td width="20"></td>
					<td width="10">
						<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
						<td>${ alt.descricao }
						<c:if test="${ alt.permiteCitacao }">
							<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
						</c:if>
						</td>
					</tr>
				</c:forEach>
			</c:if>
				
			<c:if test="${ p.multiplaEscolha }">
				<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
				<c:forEach var="alt" items="${ p.alternativas }">
					<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/></td>
					<td>${ alt.descricao }
					<c:if test="${ alt.permiteCitacao }">
						<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
					</c:if>
					</td></tr>
				</c:forEach>
			</c:if>
				
			<c:if test="${ p.simNao }">
			<tr>
				<td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
				<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ aval }"/></td>
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
		<tr><td width="100" align="center"><aval:trancamento aval="${ aval }" tid="${ t.id }" maxLength="600"/></td></tr>
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
					<td id="elgen-15" class="on" style="width: 165px;" align="center">
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
	
	<c:forEach var="dt" items="#{ avaliacaoInstitucional.docenteTurmasDiscente }">
		<tr>
			<td>
				<c:set value="" var="na"/>
				<c:if test="${dt.disciplina.excluirAvaliacaoInstitucional}">
					<c:set value="(NA)" var="na"/>
				</c:if>
				<aval:observacaoSimples aval="${ aval }" dtId="${ dt.id }" codigo="${ dt.turma.disciplina.codigo } ${na} - ${ dt.docenteNome }"  readOnly="${dt.disciplina.excluirAvaliacaoInstitucional}"/>
			</td>
		</tr>
	</c:forEach>

	<c:forEach var="t" items="#{ avaliacaoInstitucional.turmasDiscenteComMaisDeUmDocente }">
		<tr>
			<td>
				<c:set value="" var="na"/>
				<c:if test="${t.disciplina.excluirAvaliacaoInstitucional}">
					<c:set value="(NA)" var="na"/>
				</c:if>
				<aval:observacaoMultiplo aval="${ aval }" t="${ t }" codigo="${ t.disciplina.codigo } ${na}"  readOnly="${t.disciplina.excluirAvaliacaoInstitucional}"/>
			</td>
		</tr>
	</c:forEach>

</table>


<h5>Comentários gerais</h5>
<h:inputTextarea value="#{ avaliacaoInstitucional.obj.observacoes }" rows="8" cols="105" id="comment" style="width: 99%" onkeydown="limitText(this, commentCount, 600);" onkeyup="limitText(this, commentCount, 600);"/>
Você pode digitar <input readonly type="text" id="commentCount" size="3" value="${600 - fn:length(avaliacaoInstitucional.obj.observacoes) < 0 ? 0 : 600 - fn:length(avaliacaoInstitucional.obj.observacoes)}"> caracteres.

</center>

</div>
</div>

</div>


<table class="botoes">
<c:choose>
	<c:when test="${acesso.acessibilidade}">
		<tr>
			<td style="text-align: center;"><h:graphicImage value="/img/consolidacao/nav_left_red.png" style="overflow: visible;" title="Cancelar" alt="Cancelar"/></td>
			<td style="text-align: center;"><h:graphicImage value="/img/consolidacao/disk_green.png" title="Salvar" alt="Salvar" /></td>
			<td style="text-align: center;"><h:graphicImage value="/img/consolidacao/disk_blue_ok.png" title="Finalizar" alt="Finalizar" /></td>
		</tr>
	</c:when>
	<c:otherwise>
		<tr>
			<td style="text-align: center;"><h:commandButton value="Cancelar" image="/img/consolidacao/nav_left_red.png" action="#{ avaliacaoInstitucional.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a avaliação?')) { return false; }"/></td>
			<td style="text-align: center;"><h:commandButton value="Salvar" image="/img/consolidacao/disk_green.png" action="#{ avaliacaoInstitucional.salvar }"/></td>
			<td style="text-align: center;"><h:commandButton value="Finalizar" image="/img/consolidacao/disk_blue_ok.png" action="#{ avaliacaoInstitucional.finalizar }" onclick="if (!confirm('Deseja realmente finalizar a avaliação?')) { return false; }"/></td>
		</tr>
	</c:otherwise>
</c:choose>
	<tr>
		<td style="text-align: center;"><h:commandLink value="Cancelar" action="#{ avaliacaoInstitucional.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a avaliação?')) { return false; }" /></td>
		<td style="text-align: center;"><h:commandLink value="Salvar" action="#{ avaliacaoInstitucional.salvar }"  /> </td>
		<td style="text-align: center;"><h:commandLink value="Finalizar" action="#{ avaliacaoInstitucional.finalizar }" onclick="if (!confirm('Deseja realmente finalizar a avaliação?')) { return false; }"/></td>
	</tr>
</table>

</h:form>
</f:view>

<script type="text/javascript" src="${ctx}/javascript/avaliacao.js"></script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

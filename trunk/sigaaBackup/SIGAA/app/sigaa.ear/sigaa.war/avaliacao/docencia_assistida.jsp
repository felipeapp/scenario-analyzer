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

<h:form id="form">
<h:messages showDetail="true"></h:messages>

<input type="hidden" name="aba" id="aba"/>
<h2><ufrn:subSistema/> &gt; Questionário da Avaliação da Docência Assistida</h2>

<c:set var="turmasComUmDocente" value="${ formularioAvaliacaoInstitucionalBean.turmasComUmDocente }"/>
<c:set var="turmasMaisDeUmDocente" value="${ formularioAvaliacaoInstitucionalBean.turmasComMaisDeUmDocente }"/>
<c:set var="turmasDocenciaAssistida" value="${ formularioAvaliacaoInstitucionalBean.turmasDocenciaAssistida }"/>

<c:set var="aval" value="${ formularioAvaliacaoInstitucionalBean.avaliacao }"/>
<jsp:useBean id="perguntaAnterior" class="br.ufrn.sigaa.avaliacao.dominio.Pergunta"/>

<div class="descricaoOperacao">
	<h4> Caro Usuário, </h4>
	<p>Esta avaliação é parte de um processo mais amplo de avaliação do Ensino Superior, determinado pela Lei
	Federal n&ordm; 10.861/04 e pela Resolução n&ordm; 131/2008 - CONSEPE, executada pela ${ configSistema['siglaInstituicao'] } e tem em vista a melhoria das
	condições de ensino e de aprendizagem na graduação. O resultado será discutido pela comunidade acadêmica da
	${configSistema['siglaInstituicao']}. Suas respostas são de fundamental importância para a avaliação. A ${ configSistema['siglaInstituicao'] } agradece a sua participação.</p>
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
</div>

<a4j:poll action="#{ formularioAvaliacaoInstitucionalBean.salvar }" interval="300000"/>
<a4j:status startText="Salvando..."/>

<%-- GRUPOS DE PERGUNTAS --%>
<c:forEach var="grupo" items="#{ formularioAvaliacaoInstitucionalBean.grupos }" varStatus="grupoLoop">
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
			<c:set var="cabecalho" value="false" />
			<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">
				<c:if test="${ p.simNao || p.nota}">
					<c:set var="cabecalho" value="true" />
				</c:if>
			</c:forEach>		
			<c:if test="${cabecalho}">
				<table width="100%" class="turma"><tr><td width="425">&nbsp;</td>
				<c:forEach var="t" items="${ turmasComUmDocente }">
				<td width="50" align="center">
					<a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="${ t.nomeDiscente }">
						${ t.turma.disciplina.codigo } T${ t.turma.codigo }
						<c:if test="${t.turma.disciplina.excluirAvaliacaoInstitucional}">(NA)</c:if> 
					</a>
				</td>
				</c:forEach><td></td></tr>
				</table>
			</c:if>
		
			<%-- PERGUNTAS SIM/NÃO E NOTAS --%>
			<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">
				<c:if test="${ perguntaAnterior.avaliarTurmas && !p.avaliarTurmas && empty turmasMaisDeUmDocente }">
				<br/>
				<h4>Perguntas Gerais</h4>
				</c:if>
				<c:set var="naoRespondida" value="false"/>
				<c:forEach items="#{ formularioAvaliacaoInstitucionalBean.perguntasNaoRespondidas }" var="tnr" >
					<c:forEach items="#{ tnr.value }" var="idPergunta" >
						<c:if test="${idPergunta == p.id}">
							<c:set var="naoRespondida" value="true"/>
						</c:if>	
					</c:forEach>		
				</c:forEach>
			
				<table width="100%" ${ naoRespondida ? 'class="nao-respondida"' : '' }>
				
				<c:if test="${ p.simNao }">
					<c:if test="${ p.avaliarTurmas }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:forEach var="t" items="${ turmasComUmDocente }">
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="${t.turma.disciplina.excluirAvaliacaoInstitucional}"/></td>
						</c:forEach><td></td>
						</tr>
					</c:if>
					<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ aval }"  readOnly="${t.turma.disciplina.excluirAvaliacaoInstitucional}"/></td><td></td>
						</tr>
					</c:if>
				</c:if>
				
				<c:if test="${ p.nota }">
					<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:if test="${ p.avaliarTurmas }">
							<c:forEach var="t" items="${ turmasComUmDocente }" varStatus="irep">
								<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="${t.turma.disciplina.excluirAvaliacaoInstitucional}"/></td>
							</c:forEach>
						</c:if>
						<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
							<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"  readOnly="${t.turma.disciplina.excluirAvaliacaoInstitucional}"/></td>
						</c:if>
						<td></td>
					</tr>
				</c:if>	
				
				</table>
				<c:set value="${ p }" var="perguntaAnterior"/>
			</c:forEach>
			<%-- DEMAIS PERGUNTAS --%>
			<c:set var="cabecalho" value="false" />
			<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">
				<c:if test="${ p.escolhaUnica || p.multiplaEscolha}">
					<c:set var="cabecalho" value="true" />
				</c:if>
			</c:forEach>
			<c:forEach var="t" items="${ turmasComUmDocente }">
				<%-- CABECALHO COM CODIGOS DOS COMPONENTES --%>
				<c:if test="${cabecalho}">
					<table width="100%" class="turma">
						<tr class="linhaImpar">
						<td>
							<a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="${ t.nomeDiscente }">
								${ t.turma.disciplina.codigo } T${ t.turma.codigo } - Bolsista: ${ t.nomeDiscente }
								<c:if test="${t.turma.disciplina.excluirAvaliacaoInstitucional}">(NA)</c:if> 
							</a>
						</td>
						</tr>
					</table>
				</c:if>
				<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">
					<table width="100%">
					<c:if test="${ p.escolhaUnica }">
						<c:set var="naoRespondida" value="false"/>
						<c:forEach items="#{ formularioAvaliacaoInstitucionalBean.perguntasNaoRespondidas }" var="tnr" >
							<c:if test="${tnr.key == t.id}">	
								<c:forEach items="#{ tnr.value }" var="idPergunta" >
									<c:if test="${idPergunta == p.id}">
										<c:set var="naoRespondida" value="true"/>
									</c:if>	
								</c:forEach>		
							</c:if>
						</c:forEach>
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"
						  style="${ naoRespondida ? 'color: red; font-weight: bold;' : '' }">
							<td colspan="3">${ loop.index + 1 }. ${ p.descricao }<br/>
							</td>
						</tr>
						<c:forEach var="alt" items="${ p.alternativas }">
							<tr>
								<td width="5%"></td>
								<td width="3%">
									<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" tid="${ t.id }"/>
								</td>
								<td>
									${ alt.descricao }
									<c:if test="${ alt.permiteCitacao }">
										<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" tid="${ t.id }"/>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</c:if>
					
					<c:if test="${ p.multiplaEscolha }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td>
						</tr>
						<c:forEach var="alt" items="${ p.alternativas }">
							<tr>
								<td width="5%"></td>
								<td width="3%">
									<aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
								</td>
								<td>
									${ alt.descricao }
									<c:if test="${ alt.permiteCitacao }">
										<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }"/>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</c:if>
					</table>
				</c:forEach>
				<br/>
			</c:forEach>
		</c:if>

		<%-- TURMAS COM MAIS DE UM DOCENTE --%>
		<c:if test="${ not empty turmasMaisDeUmDocente }">
		<br/>
			<h4>As turmas abaixo possuem mais de um bolsista de Docência Assistida, por isso é necessário avaliar cada um deles</h4><br/>
		
			<%-- PARA CADA TURMA, LISTAR OS DOCENTES --%>
			<c:forEach var="t" items="${ turmasMaisDeUmDocente }" varStatus="countTurmas">
				<c:set var="naoRespondida" value="false"/>
				<c:forEach items="#{ formularioAvaliacaoInstitucionalBean.perguntasNaoRespondidas }" var="tnr" >
					<c:if test="${tnr.key == t.key.id}">	
						<c:forEach items="#{ tnr.value }" var="idPergunta" >
							<c:if test="${idPergunta == p.id}">
								<c:set var="naoRespondida" value="true"/>
							</c:if>	
						</c:forEach>		
					</c:if>
				</c:forEach>
				<table width="100%" ${ naoRespondida ? 'class="nao-respondida"' : '' } >
				<tr>
					<td width="425">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${t.key.id})" title="Ver Detalhes dessa turma">${ t.key.disciplina.codigoNome } Turma ${ t.key.codigo }</a>
					</td>
					<c:forEach var="dt" items="${ t.value }" varStatus="irep">
						<td width="205" style="text-align: center">
							<acronym title="${ dt.nomeDiscente }">${ dt.nomeDiscenteAbreviado }</acronym>
						</td>
					</c:forEach>
					<td></td>
				</tr>
				</table> 
			
				<%-- PERGUNTAS --%>
				<c:if test="${t.key.disciplina.excluirAvaliacaoInstitucional}"><h4>Esta disciplina não será avaliada</h4>
				</c:if>
				<c:if test="${not t.key.disciplina.excluirAvaliacaoInstitucional}">
					<c:forEach items="#{ grupo.perguntasAtivas }" var="p" varStatus="loop">
						<c:if test="${ perguntaAnterior.avaliarTurmas && !p.avaliarTurmas && countTurmas.last}">
						<br/>
						<h4>Perguntas Gerais</h4>
						</c:if>
						<c:set var="naoRespondida" value="false"/>
						<c:forEach items="#{ formularioAvaliacaoInstitucionalBean.perguntasNaoRespondidas }" var="tnr" >
							<c:forEach items="#{ tnr.value }" var="idPergunta" >
								<c:if test="${idPergunta == p.id}">
									<c:set var="naoRespondida" value="true"/>
								</c:if>	
							</c:forEach>		
						</c:forEach>
						<table width="100%" ${ naoRespondida ? 'class="nao-respondida"' : '' } >
						
						<c:if test="${ p.nota }">
							<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
								<c:if test="${ p.avaliarTurmas }">
									<c:forEach var="dt" items="${ t.value }" varStatus="irep">
										<td width="205" style="text-align: center"><aval:notas pergunta="${ p.id }" tid="${ dt.id }" aval="${ aval }"/></td>
									</c:forEach>
								</c:if>
								<c:if test="${ countTurmas.last and not p.avaliarTurmas }">
									<td width="205" style="text-align: center"><aval:notas pergunta="${ p.id }" aval="${ aval }"/></td>
								</c:if>
								<td></td>
							</tr>
						</c:if>	
						
						<c:if test="${ p.escolhaUnica }">
							<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
							<c:forEach var="alt" items="${ p.alternativas }" varStatus="loopAlt">
								<tr class="pergunta ${ loopAlt.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td width="15"></td>
									<td width="410">
										${ alt.descricao }
									</td>
									<c:forEach var="dt" items="${ t.value }" varStatus="irep">
										<td width="205" style="text-align: center">
											<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" tid="${ dt.id }"/>
											<c:if test="${ alt.permiteCitacao }">
												<br/>
												<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ aval }" tid="${ dt.id }"/>
											</c:if>
										</td>
									</c:forEach>
									<td></td>
								</tr>
							</c:forEach>
						</c:if>
						
						<c:if test="${ p.multiplaEscolha }">
							<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
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
								<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
								<c:forEach var="dt" items="${ t.value }">
								<td width="205" style="text-align: center">
									<aval:simNao pergunta="${ p.id }" tid="${ dt.id }" aval="${ aval }"/>
								</td>
								</c:forEach><td></td>
								</tr>
							</c:if>
							<c:if test="${ countTurmas.last and not p.avaliarTurmas}">
								<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
								<td width="205" style="text-align: center">
									<aval:simNao pergunta="${ p.id }" aval="${ aval }"/>
								</td>
								<td></td>
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
			<c:set var="naoRespondida" value="false"/>
			<c:forEach items="#{ formularioAvaliacaoInstitucionalBean.perguntasNaoRespondidas }" var="tnr" >
				<c:forEach items="#{ tnr.value }" var="idPergunta" >
					<c:if test="${idPergunta == p.id}">
						<c:set var="naoRespondida" value="true"/>
					</c:if>	
				</c:forEach>		
			</c:forEach>				
			<table width="100%" ${ naoRespondida ? 'class="nao-respondida"' : '' }>
				
			<c:if test="${ p.nota }">
				<tr><td>${ loop.index + 1 }. ${ p.descricao }</td>
					<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"/></td>
				</tr>
			</c:if>	
				
			<c:if test="${ p.escolhaUnica }">
				<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
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
				<tr><td colspan="3">${ loop.index + 1 }. ${ p.descricao }</td></tr>
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
				<td>${ loop.index + 1 }. ${ p.descricao }</td>
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

<h5>Comente a atuação do bolsista da Docência Assistida na disciplina.</h5>

<table width="100%">
<c:forEach var="t" items="#{ turmasDocenciaAssistida }">
<tr><td>
<c:set value="" var="na"/>
<c:if test="${t.turma.disciplina.excluirAvaliacaoInstitucional}"><c:set value="(NA)" var="na"/></c:if>
<aval:observacaoSimples aval="${ aval }" dtId="${ t.id }" codigo="${ t.turma.disciplina.codigo } T${t.turma.codigo} ${na} - ${ t.nomeDiscente }"  readOnly="${t.turma.disciplina.excluirAvaliacaoInstitucional}"/>
</td></tr>
</c:forEach>

</table>


<h5>Comentários gerais</h5>
<h:inputTextarea value="#{ formularioAvaliacaoInstitucionalBean.avaliacao.observacoes }" rows="8" cols="105" id="comment" style="width: 99%" onkeydown="limitText(this, commentCount, 600);" onkeyup="limitText(this, commentCount, 600);"/>
Você pode digitar <input readonly type="text" id="commentCount" size="3" value="${600 - fn:length(formularioAvaliacaoInstitucionalBean.avaliacao.observacoes) < 0 ? 0 : 600 - fn:length(formularioAvaliacaoInstitucionalBean.avaliacao.observacoes)}"> caracteres.

</center>

</div>
</div>


<table class="botoes">
	<tr>
		<td style="text-align: center;"><h:commandButton value="Cancelar" image="/img/consolidacao/nav_left_red.png" action="#{ formularioAvaliacaoInstitucionalBean.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a avaliação?')) { return false; }"/></td>
		<td style="text-align: center;"><h:commandButton value="Salvar" image="/img/consolidacao/disk_green.png" action="#{ formularioAvaliacaoInstitucionalBean.salvar }"/></td>
		<td style="text-align: center;"><h:commandButton value="Finalizar" image="/img/consolidacao/disk_blue_ok.png" action="#{ formularioAvaliacaoInstitucionalBean.finalizar }" onclick="if (!confirm('Deseja realmente finalizar a avaliação?')) { return false; }"/></td>
	</tr>
	<tr>
		<td style="text-align: center;"><h:commandLink value="Cancelar" action="#{ formularioAvaliacaoInstitucionalBean.cancelar }" onclick="if (!confirm('Deseja realmente cancelar a avaliação?')) { return false; }"/></td>
		<td style="text-align: center;"><h:commandLink value="Salvar" action="#{ formularioAvaliacaoInstitucionalBean.salvar }"/> </td>
		<td style="text-align: center;"><h:commandLink value="Finalizar" action="#{ formularioAvaliacaoInstitucionalBean.finalizar }" onclick="if (!confirm('Deseja realmente finalizar a avaliação?')) { return false; }"/></td>
	</tr>
</table>


</h:form>
</f:view>

<script type="text/javascript" src="${ctx}/javascript/avaliacao.js"></script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>

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

<input type="hidden" name="aba" id="aba"/>
<h2 class="title"><ufrn:subSistema /> &gt; Formulário de Avaliação Institucional</h2>

<c:set var="turmasComUmDocente" value="${ cadastrarFormularioAvaliacaoInstitucionalMBean.docenteTurmasDiscenteMock }"/>
<c:set var="turmasMaisDeUmDocente" value="${ cadastrarFormularioAvaliacaoInstitucionalMBean.turmasDiscenteComMaisDeUmDocenteMock }"/>

<c:set var="aval" value="${ cadastrarFormularioAvaliacaoInstitucionalMBean.obj }"/>
<jsp:useBean id="perguntaAnterior" class="br.ufrn.sigaa.avaliacao.dominio.Pergunta"/>

<div class="descricaoOperacao">
	<c:if test="${not empty avaliacaoInstitucional.formulario.instrucoesGerais }">
		${cadastrarFormularioAvaliacaoInstitucionalMBean.obj.instrucoesGerais}
	</c:if>
	<c:if test="${empty avaliacaoInstitucional.formulario.instrucoesGerais }">
		<h4> Caro usuário, </h4>
		<p>Esta é uma prévia de como será o formulário de Avaliação Institucional. Por favor, verifique se está conforme o planejado e caso esteja de acordo, confirme o cadastro.</p>
	</c:if>
</div>

<%-- GRUPOS DE PERGUNTAS --%>
<c:forEach var="grupo" items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.grupoPerguntas }" varStatus="grupoLoop">
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
			
				<table width="100%" >
				
				<c:if test="${ p.nota }">
					<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:if test="${ p.avaliarTurmas }">
							<c:forEach var="t" items="${ turmasComUmDocente }" varStatus="irep">
								<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ t.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
							</c:forEach>
						</c:if>
						<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
							<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }"  readOnly="true"/></td>
						</c:if>
						<td></td>
					</tr>
				</c:if>	
				
				<c:if test="${ p.escolhaUnica and empty turmasMaisDeUmDocente }">
					<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
					<c:forEach var="alt" items="${ p.alternativas }">
						<tr><td width="20"></td>
						<td width="10">
							<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
							<td>${ alt.descricao }
							<c:if test="${ alt.permiteCitacao }">
								<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/>
							</c:if>
							</td>
						</tr>
					</c:forEach>
				</c:if>
				
				<c:if test="${ p.multiplaEscolha and empty turmasMaisDeUmDocente }">
					<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
					<c:forEach var="alt" items="${ p.alternativas }">
						<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true" /></td>
						<td>${ alt.descricao }
						<c:if test="${ alt.permiteCitacao }">
							<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/>
						</c:if>
						</td></tr>
					</c:forEach>
				</c:if>
				
				<c:if test="${ p.simNao }">
					<c:if test="${ p.avaliarTurmas }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
						<c:forEach var="t" items="${ turmasComUmDocente }">
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" tid="${ t.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
						</c:forEach><td></td>
						</tr>
					</c:if>
					<c:if test="${ not p.avaliarTurmas and empty turmasMaisDeUmDocente }">
						<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
						<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }"  readOnly="true"/></td><td></td>
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
				<table width="100%">
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
							</c:when>
							<c:when test="${ not p.avaliarTurmas }">
							</c:when>
						</c:choose>
						
						<table width="100%" ${ perguntaNaoRespondida ? 'class="nao-respondida"' : '' }>
						
						<c:if test="${ p.nota }">
							<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
								<c:if test="${ p.avaliarTurmas }">
									<c:forEach var="dt" items="${ t.docentesTurmas }" varStatus="irep">
										<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ dt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }"/></td>
									</c:forEach>
								</c:if>
								<c:if test="${ countTurmas.last and not p.avaliarTurmas }">
									<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }"/></td>
								</c:if>
								<td></td>
							</tr>
						</c:if>	
						
						<c:if test="${ p.escolhaUnica and countTurmas.last }">
							<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
							<c:forEach var="alt" items="${ p.alternativas }">
								<tr><td width="20"></td>
								<td width="10">
									<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
									<td>${ alt.descricao }
									<c:if test="${ alt.permiteCitacao }">
										<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/>
									</c:if>
									</td>
								</tr>
							</c:forEach>
						</c:if>
						
						<c:if test="${ p.multiplaEscolha and countTurmas.last }">
							<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
							<c:forEach var="alt" items="${ p.alternativas }">
								<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
								<td>${ alt.descricao }
								<c:if test="${ alt.permiteCitacao }">
									<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/>
								</c:if>
								</td></tr>
							</c:forEach>
						</c:if>
						
						<c:if test="${ p.simNao }">
							<c:if test="${ p.avaliarTurmas }">
								<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
								<c:forEach var="dt" items="${ t.docentesTurmas }">
								<td width="50" align="center"><aval:simNao pergunta="${ p.id }" tid="${ dt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
								</c:forEach><td></td>
								</tr>
							</c:if>
							<c:if test="${ countTurmas.last and not p.avaliarTurmas}">
								<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
								<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td><td></td>
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
			<table width="100%" >
				
			<c:if test="${ p.nota }">
				<tr><td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
					<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
					<td></td>
				</tr>
			</c:if>	
				
			<c:if test="${ p.escolhaUnica }">
				<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
				<c:forEach var="alt" items="${ p.alternativas }">
					<tr><td width="20"></td>
					<td width="10">
						<aval:escolhaUnica pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
						<td>${ alt.descricao }
						<c:if test="${ alt.permiteCitacao }">
							<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/>
						</c:if>
						</td>
					</tr>
				</c:forEach>
			</c:if>
				
			<c:if test="${ p.multiplaEscolha }">
				<tr><td colspan="3" width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td></tr>
				<c:forEach var="alt" items="${ p.alternativas }">
					<tr><td width="20"></td><td width="10"><aval:multiplaEscolha pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
					<td>${ alt.descricao }
					<c:if test="${ alt.permiteCitacao }">
						<aval:citacao pergunta="${ p.id }" alternativa="${ alt.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/>
					</c:if>
					</td></tr>
				</c:forEach>
			</c:if>
				
			<c:if test="${ p.simNao }">
			<tr>
				<td width="${tamanhoColunaPergunta}">${ loop.index + 1 }. ${ p.descricao }</td>
				<td width="50" align="center"><aval:simNao pergunta="${ p.id }" aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" readOnly="true"/></td>
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

<c:if test="${ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.avaliacaoDiscente }">
	<center>
	
	<h5>Deseja comentar sobre quais disciplinas/professores?</h5>
	
	<table width="100%">
	<c:forEach var="dt" items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.docenteTurmasDiscenteMock }">
	<tr><td>
	<c:set value="" var="na"/>
	<c:if test="${dt.disciplina.excluirAvaliacaoInstitucional}"><c:set value="(NA)" var="na"/></c:if>
	<aval:observacaoSimples aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" dtId="${ dt.id }" codigo="${ dt.turma.disciplina.codigo } ${na} - ${ dt.docenteNome }"  readOnly="true"/>
	</td></tr>
	</c:forEach>
	
	<c:forEach var="t" items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.turmasDiscenteComMaisDeUmDocenteMock }">
	<tr><td>
	<c:set value="" var="na"/>
	<c:if test="${t.disciplina.excluirAvaliacaoInstitucional}"><c:set value="(NA)" var="na"/></c:if>
	<aval:observacaoMultiplo aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" t="${ t }" codigo="${ t.disciplina.codigo } ${na}"  readOnly="true"/>
	</td></tr>
	</c:forEach>
	
	</table>
	
	
	<h5>Comentários gerais</h5>
	<h:inputTextarea rows="8" cols="105" id="comment" style="width: 99%" onkeydown="limitText(this, commentCount, 600);" onkeyup="limitText(this, commentCount, 600);" readonly="true"/>
	Você pode digitar 600 caracteres.
	
	</center>
</c:if>
<c:if test="${ not cadastrarFormularioAvaliacaoInstitucionalMBean.obj.avaliacaoDiscente }">
	<center>
	<c:if test="${ not empty cadastrarFormularioAvaliacaoInstitucionalMBean.docenteTurmasDiscenteMock }">
		<h5>Comentários Específicos sobre a Turma (marque a turma específica)</h5>
	</c:if>
	
	<table width="100%">
	<c:forEach var="dt" items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.docenteTurmasDiscenteMock }">
	<tr><td>
	<c:set value="" var="na"/>
	<c:if test="${dt.disciplina.excluirAvaliacaoInstitucional}"><c:set value="(NA)" var="na"/></c:if>
	<aval:observacaoSimples aval="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }" dtId="${ dt.id }" codigo="${ dt.turma.disciplina.descricao } ${na}"  readOnly="true"/>
	</td></tr>
	</c:forEach>
	</table>
	<h5>Comentários gerais</h5>
	<h:inputTextarea rows="8" cols="105" id="comment" style="width: 99%" onkeydown="limitText(this, commentCount, 600);" onkeyup="limitText(this, commentCount, 600);" readonly="true"/>
	Você pode digitar 600 caracteres.
	</center>
</c:if>

</div>
</div>

</div>


<table class="formulario" width="100%">
<tfoot>
	<tr>
		<td style="text-align: center;">
			<h:commandButton value="#{cadastrarFormularioAvaliacaoInstitucionalMBean.confirmButton}" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.cadastrar }" />
			<h:commandButton value="<< Voltar" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.formCadastro }" rendered="#{ !cadastrarFormularioAvaliacaoInstitucionalMBean.readOnly }" />
			<h:commandButton value="Cancelar" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.cancelar }" onclick="#{ confirm }" />
		</td>
	</tr>
</tfoot>
</table>

</h:form>
</f:view>

<script type="text/javascript" src="${ctx}/javascript/avaliacao.js"></script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<link rel="stylesheet" href="${ctx}/css/avaliacao-institucional.css" type="text/css"/>

<f:view>
<h:form>

${ avaliacaoInstitucional.verificaAcessoDiscente }

<h2><a href="${ctx}/verPortalDocente.do">Portal Docente</a> &gt; Questionário da Avaliação da Docência pelo Professor</h2>

<c:set var="turmas" value="${ avaliacaoInstitucional.turmasDocente }"/>
<c:set var="aval" value="${ avaliacaoInstitucional.avaliacaoAnterior }"/>
<jsp:useBean id="perguntaAnterior" class="br.ufrn.sigaa.avaliacao.dominio.Pergunta"/>

<div class="descricaoOperacao">
<p>Você está visualizando a avaliação do ano-período anterior, não sendo possível alterá-la.</p>
</div>

<c:forEach var="grupo" items="#{ avaliacaoInstitucional.gruposDocente }">
<div id="dimensoes-avaliacao-${ grupo.id }" class="reduzido">
	<div id="elgen-8" class="ytab-wrap">
		<div class="ytab-strip-wrap">
		<table id="elgen-10" class="ytab-strip" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr id="elgen-9" class="">
					<td id="elgen-15" class="on" style="width: 280px;" align="center">
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

	<c:if test="${ grupo.avaliaTurmas }">
	<table width="100%" class="turma"><td width="420">&nbsp;</td>
	<c:forEach var="t" items="${ turmas }">
	<td width="50" align="center"><a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="Ver Detalhes dessa turma">${ t.disciplina.codigo }</a></td>
	</c:forEach><td></td>
	</table>
	</c:if>

	<c:forEach items="#{ grupo.perguntas }" var="p" varStatus="loop">
	
	<c:if test="${ (not grupo.avaliaTurmas) && (p.avaliarTurmas) && (perguntaAnterior.id == 0 || not perguntaAnterior.avaliarTurmas) }">
	<table width="100%" class="turma"><td width="425">&nbsp;</td>
	<c:forEach var="t" items="${ turmas }">
	<td width="50" align="center"><a href="javascript:void(0);" onclick="PainelTurma.show(${t.turma.id})" title="Ver Detalhes dessa turma">${ t.disciplina.codigo }</a></td>
	</c:forEach><td></td>
	</table>
	</c:if>
	
	<table width="100%" ${ sf:contains(avaliacaoInstitucional.perguntasNaoRespondidas, p.id) ? 'class="nao-respondida"' : '' }>
	
	<c:if test="${ p.nota }">
		<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="420">${ loop.index + 1 }. ${ p.descricao }</td>
			<c:if test="${ p.avaliarTurmas }">
				<c:forEach var="t" items="${ turmas }" varStatus="irep">
					<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="true"/></td>
				</c:forEach>
			</c:if>
			<c:if test="${ not p.avaliarTurmas }">
				<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"  readOnly="true"/></td>
			</c:if>
			<td></td>
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
		<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
		<c:if test="${ p.avaliarTurmas }">
			<c:forEach var="t" items="${ turmas }">
			<td width="50"><aval:simNao pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }" readOnly="true"/></td>
			</c:forEach>
		</c:if>
		<c:if test="${ not p.avaliarTurmas }">
			<td width="50"><aval:simNao pergunta="${ p.id }" aval="${ aval }" readOnly="true"/></td>
		</c:if><td></td>
		</tr>
	</c:if>
	
	</table>
	<c:set value="${ p }" var="perguntaAnterior"/>
	</c:forEach>
	
	<br/>
	</div>
	</div><br/>
</c:forEach>


<div id="dimensao-comentario" class="reduzido">
	<div id="elgen-8" class="ytab-wrap">
		<div class="ytab-strip-wrap">
		<table id="elgen-10" class="ytab-strip" cellspacing="0" cellpadding="0" border="0">
			<tbody>
				<tr id="elgen-9" class="">
					<td id="elgen-15" class="on" style="width: 280px;" align="center">
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
<h:inputTextarea value="#{ aval.observacoes }" rows="6" cols="120" disabled="true"/>
</center>

</div>
</div>

</h:form>
<br/>
<center><a href="javascript:history.go(-1)"> << Voltar</a></center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript" src="${ctx}/javascript/avaliacao.js"></script>

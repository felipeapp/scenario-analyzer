<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
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

<h2 class="title"><ufrn:subSistema /> &gt; Formul�rio de Avalia��o Institucional</h2>

<c:set var="turmas" value="${ cadastrarFormularioAvaliacaoInstitucionalMBean.docenteTurmasDiscenteMock }"/>
<c:set var="aval" value="${ cadastrarFormularioAvaliacaoInstitucionalMBean.avaliacao }"/>
<jsp:useBean id="perguntaAnterior" class="br.ufrn.sigaa.avaliacao.dominio.Pergunta"/>

<div class="descricaoOperacao">
	<c:if test="${not empty cadastrarFormularioAvaliacaoInstitucionalMBean.obj.instrucoesGerais }">
		${cadastrarFormularioAvaliacaoInstitucionalMBean.obj.instrucoesGerais}
	</c:if>
	<c:if test="${empty cadastrarFormularioAvaliacaoInstitucionalMBean.obj.instrucoesGerais }">
		<h4> Caro usu�rio, </h4>
		<p>Esta � uma pr�via de como ser� o formul�rio de Avalia��o Institucional. Por favor, verifique se est� conforme o planejado e caso esteja de acordo, confirme o cadastro.</p>
	</c:if>
</div>

<c:forEach var="grupo" items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.obj.grupoPerguntas }">
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
	
	<table width="100%" >
	
	<c:if test="${ p.nota }">
		<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="420">${ loop.index + 1 }. ${ p.descricao }</td>
			<c:if test="${ p.avaliarTurmas }">
				<c:forEach var="t" items="${ turmas }" varStatus="irep">
					<td width="50" align="center"><aval:notas pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }"/></td>
				</c:forEach>
			</c:if>
			<c:if test="${ not p.avaliarTurmas }">
				<td width="50" align="center"><aval:notas pergunta="${ p.id }" aval="${ aval }"/></td>
			</c:if>
			<td></td>
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
		<tr class="pergunta ${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td width="425">${ loop.index + 1 }. ${ p.descricao }</td>
		<c:if test="${ p.avaliarTurmas }">
			<c:forEach var="t" items="${ turmas }">
			<td width="50"><aval:simNao pergunta="${ p.id }" tid="${ t.id }" aval="${ aval }"/></td>
			</c:forEach>
		</c:if>
		<c:if test="${ not p.avaliarTurmas }">
			<td width="50"><aval:simNao pergunta="${ p.id }" aval="${ aval }"/></td>
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
					<td id="elgen-15" class="on" style="width: 165px;" align="center">
						<a id="elgen-12" class="ytab-right" href="#"> 
						<span class="ytab-left"> 
						<em id="elgen-13" class="ytab-inner">
						<span id="elgen-14" class="ytab-text" title="Coment�rios Adicionais" unselectable="on">Coment�rios Adicionais</span> 
						</em></span>
						</a>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
	</div>
	
<div id="comentario" class="aba">

<h3>ESPA�O DESTINADO PARA COMENT�RIOS OPCIONAIS</h3>

<center>
<c:if test="${ not empty cadastrarFormularioAvaliacaoInstitucionalMBean.docenteTurmasDiscenteMock }">
	<h5>Coment�rios Espec�ficos sobre a Turma (marque a turma espec�fica)</h5>
</c:if>

<table width="100%">
<c:forEach var="dt" items="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.docenteTurmasDiscenteMock }">
<tr><td>
<c:set value="" var="na"/>
<c:if test="${dt.disciplina.excluirAvaliacaoInstitucional}"><c:set value="(NA)" var="na"/></c:if>
<aval:observacaoSimples aval="${ aval }" dtId="${ dt.id }" codigo="${ dt.turma.disciplina.descricao } ${na}"  readOnly="true"/>
</td></tr>
</c:forEach>
</table>
<h5>Coment�rios Gerais</h5>
<h:inputTextarea rows="8" cols="105" id="comment" style="width: 99%" onkeydown="limitText(this, commentCount, 600);"/>
Voc� pode digitar <input readonly type="text" id="commentCount" size="3" value="${600 - fn:length(avaliacaoInstitucional.obj.observacoes) < 0 ? 0 : 600 - fn:length(avaliacaoInstitucional.obj.observacoes)}"> caracteres.
</center>

</div>
</div>


<table class="formulario" width="100%">
<tfoot>
	<tr>
		<td style="text-align: center;">
			<h:commandButton value="#{cadastrarFormularioAvaliacaoInstitucionalMBean.confirmButton }" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.cadastrar }" />
			<h:commandButton value="<< Voltar" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.formCadastro }" rendered="#{ !cadastrarFormularioAvaliacaoInstitucionalMBean.readOnly }" />
			<h:commandButton value="Cancelar" action="#{ cadastrarFormularioAvaliacaoInstitucionalMBean.cancelar }" onclick="#{ confirm }" />
		</td>
	</tr>
</tfoot>
</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript" src="${ctx}/javascript/avaliacao.js"></script>

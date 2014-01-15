<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<script type="text/javascript">
<!--
var PainelConsultaTurmas = (function() {
	var painel;
	return {
        show : function(expressao){
	        	var p = getEl('painel-turma-consulta-turmas');
	        	if (p)
	        		p.remove();

     	 		painel = new YAHOO.ext.BasicDialog("painel-turma-consulta-turmas", {
	   	 		   autoCreate: true,
				   title: 'Turmas Abertas Encontradas',
                   proxyDrag: true,
                   constraintoviewport: false,
	               width: 700,
	               height: 330,
	               resizable: true
            	});

	       	 	painel.show();

				var um = painel.body.getUpdateManager();
				um.disableCaching = false;
				um.update({
					 url: '/sigaa/ensino/formacao_complementar/matricula/painel_turmas_abertas.jsf?expressao='+expressao,
					 discardUrl: true,
					 nocache: true,
					 text: 'Aguarde! Carregando Turmas...'
					 });
        }
	};
})();

	function esconderMostrar(comp, link) {
		var trs = $$('#lista-turmas-curriculo tr.turmas'+comp);
	
		for (i=0;i<trs.length;i++) {
			var linha = trs[i];
			var d = linha.style.display;
			if (d == 'none') {
				getEl(linha).show();
				link.innerHTML = '[ - ]';
			} else {
				getEl(linha).hide();
				link.innerHTML = '[ + ]';
			}
		}
	}
//
--></script>

<c:set var="confirmacaoMatricula" 
	value="return confirm('Deseja realmente matricular-se nessa turma? Esta operação não poderá ser desfeita.');"
	scope="request"/>

<f:view>
<style>
	span.periodo {
		color: #292;
		font-weight: bold;
	}

	descricaoOperacao p{
		line-height: 1.25em;
		margin: 8px 10px;
	}
	
</style>
	<h2>
		<ufrn:subSistema /> &gt; Matrícula de Discente
	</h2>
	<div class="descricaoOperacao">
		<h:form id="formDescricao">
			<h4>Caro(a) Aluno(a),</h4> <br />
			<p>
				O período de matrícula on-line estende-se de
				<span class="periodo">
					<ufrn:format type="data" valor="${matriculaFormacaoComplementarMBean.calendarioParaMatricula.inicioMatriculaOnline}"/>
			 		a <b><ufrn:format type="data" valor="${matriculaFormacaoComplementarMBean.calendarioParaMatricula.fimMatriculaOnline}"/></b>.
			 	</span>
			 	<c:if test="${matriculaFormacaoComplementarMBean.calendarioParaMatricula.periodoReMatricula}">
				 	(com a matrícula em vagas remanescentes de
				 	<span class="periodo">
				 	<b><ufrn:format type="data" valor="${matriculaFormacaoComplementarMBean.calendarioParaMatricula.inicioReMatricula}"/>
			 		a <ufrn:format type="data" valor="${matriculaFormacaoComplementarMBean.calendarioParaMatricula.fimReMatricula}"/></b>)
			 		</span>
			 	</c:if>
			 	Durante esse período você poderá efetuar sua matrícula na turma desejada, de acordo com a oferta de turmas para o semestre. 
			</p>
			
			<h:outputText escape="false" value="#{matriculaFormacaoComplementarMBean.instrucoes}" rendered="#{not empty matriculaFormacaoComplementarMBean.instrucoes}"/>
			
			<br/>
			<p>
			Abaixo estão listadas todas as turmas de disciplinas do seu currículo abertas para o semestre atual (${calendarioAcademico.anoPeriodo}).
			Selecione uma turma da lista abaixo para se matricular, clicando no ícone <img src="/sigaa/img/avaliar.gif"> correspondente da turma desejada.
			</p>
			<br/>
			Dúvidas sobre as disciplinas do seu currículo?
			<h:commandLink action="#{estruturaCurricularTecnicoMBean.view}" value="Clique Aqui" target="_blank">
				<f:param name="id" value="#{matriculaFormacaoComplementarMBean.discente.estruturaCurricularTecnica.id}" />
			</h:commandLink>
			para ver os detalhes de sua estrutura curricular.
		</h:form>
	</div>  
	<%@ include file="_info_discente.jsp"%>

	<%-- Legenda --%>
	<div class="infoAltRem">
		<img src="/sigaa/img/graduacao/matriculas/matricula_permitida.png" alt="*">: É permitida a matrícula nesse componente
		<img src="/sigaa/img/graduacao/matriculas/matricula_negada.png">: Não é permitida a matrícula nesse componente <br />
		<img src="/sigaa/img/graduacao/matriculas/zoom.png">: Ver detalhes da turma
		<img src="/sigaa/img/avaliar.gif">: Matricular-se nessa turma
	</div>

	<h:form id="formMatricula">
		<table class="listagem" id="lista-turmas-curriculo" >
			<caption>Turmas Abertas para os Componentes do seu Currículo</caption>

			<thead>
			<tr>
				<th colspan="2"> </th>
				<th> Turma </th>
				<th width="30%"> Docente(s) </th>
				<th width="20%"> Horário </th>
				<th> Local </th>
				<th>  </th>
			</tr>
			</thead>
			
			<tbody>
			<c:if test="${empty matriculaFormacaoComplementarMBean.turmasCurriculo}">
				<tr>
					<td colspan="7" align="center">
						Não foi possível encontrar sugestões de matrícula para esse discente.<br/>
					</td>
				</tr>
			</c:if>

			<c:set var="semestreAtual" value="0" />
			<c:set var="disciplinaAtual" value="0" />
			<h:outputText value="#{util.create}"></h:outputText>
			<c:forEach items="#{matriculaFormacaoComplementarMBean.turmasCurriculo}" var="sugestao" varStatus="status">

				<%-- Semestre de Oferta --%>
				<c:if test="${ semestreAtual != sugestao.nivel}">
					<c:set var="semestreAtual" value="${sugestao.nivel}" />
				</c:if>

				<%-- Componente Curricular --%>
				<c:if test="${ disciplinaAtual != sugestao.turma.disciplina.id}">
					<c:set var="disciplinaAtual" value="${sugestao.turma.disciplina.id}" />
					<tr class="disciplina" >
						<td width="3%">
						<c:if test="${not sugestao.podeMatricular}">
						<ufrn:help img="/img/graduacao/matriculas/matricula_negada.png" width="400">
							<li>${sugestao.motivoInvalido }</li>
						</ufrn:help>
						</c:if>
						<c:if test="${sugestao.podeMatricular}">
							<img alt="" src="/sigaa/img/graduacao/matriculas/matricula_permitida.png">
						</c:if>
						</td>
						<td colspan="4" style="font-size: xx-small">
							<a href="javascript:void(0);" style="${sugestao.podeMatricular?'':'color: #666'}"
								onclick="PainelComponente.show(${sugestao.turma.disciplina.id}, '#nivel_${sugestao.nivel}')" title="Ver Detalhes do Componente Curricular">
							<c:choose>
								<c:when test="${not sugestao.podeMatricular}">
									${sugestao.turma.disciplina.codigo} - ${sugestao.turma.disciplina.nome}
								</c:when>
								<c:otherwise>
									* ${sugestao.turma.disciplina.codigo} - ${sugestao.turma.disciplina.nome}
								</c:otherwise>
							</c:choose>
							</a>
							<c:if test="${not empty sugestao.obrigatoriaDescricao}">
							<span style="font-size: xx-small; font-style: italic;">(${sugestao.obrigatoriaDescricao})</span>
							</c:if>
							<c:if test="${not sugestao.podeMatricular}">
							<span onclick="esconderMostrar(${sugestao.turma.disciplina.id})" onmouseover="this.style.cursor='pointer'" title="exibir/esconder turmas">
							[ + ]
							</span>
							</c:if>
						</td>
						<td style="font-size: 9px; font-family: Arial; font-weight: normal; text-align: right;" colspan="2">
							<c:if test="${sugestao.buscarEquivalentes}">
								[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.equivalencia}')" title="Mostrar disciplinas equivalentes">Equivalentes</a>]
								<br />
							</c:if>
							<c:if test="${sugestao.buscarPreRequisitos}">
								[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.preRequisito}')" title="Mostrar disciplinas de pré-requisitos">Pré-requisitos</a>]
								<br />
							</c:if>
							<c:if test="${sugestao.buscarCoRequisitos}">
								[<a href="javascript:void(0);" class="linkExpressoes" onclick="PainelConsultaTurmas.show('${sugestao.turma.disciplina.coRequisito}')" title="Mostrar disciplinas de pré-requisitos">Co-requisitos</a>]
								<br />
							</c:if>
						</td>
					</tr>
				</c:if>

				<%-- Turma --%>
				<c:set value="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" var="stLinha" />
				<tr class="${stLinha} turmas${sugestao.turma.disciplina.id}" style="display: ${sugestao.podeMatricular ? 'table-row':'none'}"
						id="cc_${sugestao.turma.disciplina.id}t_${sugestao.turma.codigo}s_${sugestao.nivel}TR">
					<td>
					</td>
					<td width="2%">
						<c:if test="${sugestao.podeMatricular}">
						<a href="javascript:void(0);" onclick="PainelTurma.show(${sugestao.turma.id})" title="Ver Detalhes dessa turma">
							<img src="/sigaa/img/graduacao/matriculas/zoom.png" alt=""
								class="noborder" />
						</a>
						</c:if>
					</td>
					
					<td width="11%" style="font-size: xx-small">
						Turma ${sugestao.turma.codigo}
					</td>
					<td style="font-size: xx-small">
						
						<c:if test="${not empty sugestao.turma.observacao}">
						   <strong><ufrn:format valor="${sugestao.turma.observacao}" type="texto" length="60"/></strong> - 
						</c:if>
						<c:if test="${not empty sugestao.turma.especializacao.descricao}">
						  	<strong>${sugestao.turma.especializacao.descricao}</strong> - 
						</c:if>
						${sugestao.docentesNomes}
						
					</td>
					<td width="10%" style="font-size: xx-small">
						${sugestao.turma.descricaoHorario}
					</td>
					<td width="7%" style="font-size: xx-small">
						${sugestao.turma.local}
					</td>
					<td width="2%">
						<h:commandLink id="selecionarTurma" title="Selecionar turma" rendered="#{sugestao.podeMatricular}" 
								action="#{matriculaFormacaoComplementarMBean.selecionarTurma}" onclick="#{confirmacaoMatricula}">
							<f:param name="idTurma" value="#{sugestao.turma.id}"/>
							<h:graphicImage url="/img/avaliar.gif" alt="Matricular-se nessa turma" title="Matricular-se nessa turma"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="7" style="text-align: center;">
						<h:commandButton id="btnCancelar" value="Cancelar" action="#{matriculaFormacaoComplementarMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
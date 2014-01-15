<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_medio.js"></script>
<script type="text/javascript" src="/shared/javascript/jquery/jquery-1.4.4.min.js"></script>
<script type="text/javascript" charset="ISO-8859">
	var J = jQuery.noConflict();
</script>
<script type="text/javascript" charset="ISO-8859-1">
//Verifica se a nota digitada é maior que 10. Se for, invalida.
function verificaNotaMaior(campo) {
	var valor = parseFloat(campo.value.replace(',','.'));
	if (valor > 10) {
		alert('Nota incorreta. As notas devem estar entre 0 e 10.');
		campo.value = '';
		return true;
	}
	return false;
}

function transferir(elem,ordem) {
	var linha = getEl(elem.parentNode.parentNode);
	var index;
	if (ordem <= 2)
		index = ordem-1;
	else
		index = ordem-2;
	var elOrigem  = linha.getChildrenByClassName('faltas-calc')[index];
	var elDestino = linha.getChildrenByClassName('faltas')[index];
	elDestino.dom.value = elOrigem.dom.value;
}

function transferirTodos(ordem) {
	var els = getEl('notas_turma').getChildrenByClassName('img-transferir-'+ordem);
	for (var i = 0; i < els.length; i++) {
		els[i].dom.onclick();
	}
}

</script>
<f:view>
<a4j:keepAlive beanName="consolidarDisciplinaMBean"/>
<h2 style="font-size:1.2em;padding:4px 0 2px 2px;border-bottom:1px solid;margin:0 0 5px;" >
	<ufrn:subSistema/> &gt; Cadastro de Notas 
</h2>
<h3>${ consolidarDisciplinaMBean.descricaoTurma }</h3>
<hr/>
<fmt:setLocale value="pt_BR"/>
<h:form id="form" prependId="false">
<a4j:poll action="#{ consolidarDisciplinaMBean.salvarNotas }" interval="300000"/>

	<input type="hidden" name="idTurma"  value="${ consolidarDisciplinaMBean.turma.id }"/>
	
	<div class="descricaoOperacao">
		<ul style="margin-top: 0; padding-left: 0">
			<li>Digite as notas dos bimestres utilizando ponto para separar a casa decimal.</li>
			<li>O campo faltas deve ser preenchido com o número de faltas do aluno durante o bimestre.</li>
			<li>Os resultados não vão para o histórico do aluno, no entanto, aparecem em seu portal.</li>
			<li>Clique em Salvar para gravar as notas inseridas e continuá-las posteriormente.</li>
		</ul>
	</div>
		
	<style>
		table#notas_turma tbody tr td{
			text-align:center;
		}
		
		#thBorda {border-left: 1px solid #888; border-bottom: 1px solid #888; text-align: center;}
		#thMedia {border-left: 1px solid #888; text-align: center;}
		#thFaltas {border-left: 1px solid #888; text-align: center;}
		#tdNota {border-left: 1px solid #888; text-align: center; width: 3%}
		#tdFaltas {text-align: center; width: 1%}
		#tdFaltasTotal {border-left: 1px solid #888; text-align: center; width: 1%}
		
	</style>
	<div class="adInfo" align="center" style="display: none;">
		<p>
			 Ao salvar as notas, elas não serão divulgadas aos alunos. 
		 	 É possível <b>publicar as notas</b> salvas dos alunos ao configurar a turma virtual. 
		 	 Para isso, clique <h:commandLink value="aqui" action="#{consolidarTurma.iniciarConfiguracoesAva}"/> e marque "Não" na opção "Ocultar as notas dos alunos",
		 	 ou clique no botão "Salvar e Publicar" 
		</p>
	</div>
	
	<div class="notas" style="clear: both;">
		<c:set var="regrasNotas" value="#{consolidarDisciplinaMBean.regraNotas}"/>
		<table class="listagem" style="width:140%" id="notas_turma">
		<caption>Alunos Matriculados</caption>
		<thead>
			<tr>
				<th rowspan="2" width="5%"><p style="text-align: center;padding-right:5px;">Matrícula</p></th>
				
				<th rowspan="2" width="15%">Nome <input type="hidden" name="unidade" id="unidade"/></th>
				
				<c:set var="provaFinal" value=""/>
				<c:forEach items="#{regrasNotas}" var="item">
					<h:outputText rendered='#{item.recuperacao}' value="<th rowspan='2' id='thBorda'><span title='Média Parcial'>Média Parc.</span></th>" escape="false" />
					<c:if test="${!item.provaFinal}">
						<c:if test="${consolidarDisciplinaMBean.turma.consolidada}">
							<th colspan="${item.nota ? '2' : '1'}" rowspan="${item.recuperacao ? '2' : '1'}" id="thBorda">
								<h:outputText value="#{item.titulo}"/>
							</th>
						</c:if>	
						<c:if test="${!consolidarDisciplinaMBean.turma.consolidada}">
							<th colspan="${item.nota ? '4' : '1'}" rowspan="${item.recuperacao ? '2' : '1'}" id="thBorda">
								<h:outputText value="#{item.titulo}"/>
							</th>
						</c:if>
					</c:if>
					<h:outputText rendered='#{item.recuperacao}' value="<th rowspan='2' id='thBorda'><span title='Média Semestral'>Média Sem.</span></th>" escape="false" />
					<c:if test="${item.provaFinal}">
						<c:set var="provaFinal" value="#{item.titulo}"/>		
					</c:if>
				</c:forEach>
				<th colspan="${not empty provaFinal ? '3' : '2'}" id="thBorda">MÉDIA</th>
				<th rowspan="2" id="thFaltas">Faltas</th>
				<th rowspan="2" id="thFaltas">Sit.</th>
			</tr>
			<tr>		
				<c:forEach items="#{regrasNotas}" var="item">
					<c:if test="${item.nota}">
						<th id="thMedia">Média</th>
						<c:if test="${item.nota}">
							<c:if test="${!consolidarDisciplinaMBean.turma.consolidada}">
								<th style="text-align: center;">Faltas Calc.</th>	
								<th><img src="${ctx}/img/consolidacao/arrow_right.png" style="cursor: pointer;" onclick="transferirTodos('${item.ordem}')"/></th>	
							</c:if>
							<th>Faltas</th>							
						</c:if>
					</c:if>
				</c:forEach>			
				<th id="thMedia">Anual</th>	
				<c:if test="${not empty provaFinal}">
					<th>${provaFinal}</th>
				</c:if>
				<th>Final</th>	
			</tr>
		</thead>
	
		<tbody>
		<c:forEach items="#{consolidarDisciplinaMBean.notasDisciplina}" var="itemDisc"  varStatus="i">
			<tr id="linha_${ i.index }" class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td nowrap="nowrap" class="disciplina">
					${itemDisc.matricula.discente.matricula}
					
					<h:graphicImage value="/img/consolidacao/situacao_#{ fn:toLowerCase(itemDisc.matricula.situacaoAbrev) }.png" 
						id="imgStatus_${itemDisc.matricula.id}" rendered="#{itemDisc.matricula.situacaoAbrev != '--' }"
						alt="#{empty itemDisc.matricula.situacaoAbrev ? '' : itemDisc.matricula.situacaoAluno}"
						title="#{empty itemDisc.matricula.situacaoAbrev ? '' : itemDisc.matricula.situacaoAluno}"/>

				</td>
				<td class="disciplina" style="text-align: left;">${itemDisc.matricula.discente.nome}</td>

				<c:set var="notaRecFinal" value="-"/>
				
				<c:forEach items="#{regrasNotas}" var="regra" varStatus="r">
					
					<c:if test="${regra.nota || regra.recuperacao}">
					
						<c:forEach items="#{itemDisc.notas}" var="itemNota">
							<c:if test="${itemNota.regraNota.id == regra.id}">
								<c:set var="idNota" value="#{itemNota.notaUnidade.id}"/>
								
								<td id="tdNota" align="center" <h:outputText value='class="destaqueCinza"' escape='false' rendered='#{ regra.recuperacao }' />>
								
									<c:if test="${ regra.recuperacao }">
										<h:outputText value="#{itemDisc.mediaSemestral1}" id="medS1_${itemDisc.matricula.id}" converter="convertNota" rendered='#{ regra.refRec == "1,2" }' />
										<h:outputText value="#{itemDisc.mediaSemestral2}" id="medS2_${itemDisc.matricula.id}" converter="convertNota" rendered='#{ regra.refRec == "4,5" }' />
										<h:outputText value='</td><td id="tdNota" class="destaqueCinza" align="center">' escape='false' />
									</c:if>
								
									<c:if test="${itemDisc.matricula.consolidada}">
										<h:outputText value="#{itemNota.notaUnidade.nota}" converter="convertNota"/>
									</c:if>
									
									<c:if test="${!itemDisc.matricula.consolidada}">
										
											<h:inputText id="nota_${itemDisc.matricula.id}_${itemNota.regraNota.id}${regra.recuperacao ? '_rec' : '' }" maxlength="4" 
												style="text-align: center; #{regra.recuperacao && !itemNota.podeEditar ? 'background-color:#F0F0F0' : 'background-color:#FFFFFF'}" 
												onkeydown="return(formataValorNota(this, event, 1));" onkeyup="verificaNotaMaior(this);atualizarNotas#{ itemDisc.matricula.id }(false);" disabled="#{regra.recuperacao && !itemNota.podeEditar}"
												value="#{itemNota.notaUnidade.nota}" size="3" immediate="true" title="Nota #{regra.titulo}">
											</h:inputText>
											
	
										
										<a4j:jsFunction reRender="nota_#{itemDisc.matricula.id}_#{itemNota.regraNota.id}_rec" 
											name="atualizaRegra_nota_#{itemDisc.matricula.id}_#{itemNota.regraNota.id}_rec" 
											rendered="#{regra.recuperacao}"/>	
										
									</c:if>
									
									<c:if test="${ regra.recuperacao }">
										<h:outputText value='</td><td id="tdNota" align="center" class="destaqueCinza">' escape='false' />
										<h:outputText value="#{itemDisc.mediaSemestral1ComRecuperacao}" id="medS1R_${itemDisc.matricula.id}" converter="convertNota" rendered='#{ regra.refRec == "1,2" }' />
										<h:outputText value="#{itemDisc.mediaSemestral2ComRecuperacao}" id="medS2R_${itemDisc.matricula.id}" converter="convertNota" rendered='#{ regra.refRec == "4,5" }' />
									</c:if>							
								</td>	
								
								<c:if test="${regra.nota}">
									<c:if test="${!consolidarDisciplinaMBean.turma.consolidada}">
										<td class="nota" id="tdFaltas">
											<c:if test="${!itemDisc.matricula.consolidada}">
											<input type="text" value="${ itemNota.notaUnidade.faltasCalc }" size="1" readonly="readonly" style="background: #FDF3E1;" class="faltas-calc"/>
											</c:if>
										</td>
										<td class="nota" id="tdFaltas">
											<c:if test="${!itemDisc.matricula.consolidada}">
											<img title="Transferir as faltas calculadas da lista de freqüência para o campo de faltas do aluno" style="cursor: pointer;" src="${ctx}/img/consolidacao/arrow_right.png" class="img-transferir-${ itemNota.regraNota.ordem}" onclick="transferir(this,'${ itemNota.regraNota.ordem}');"/>
											</c:if>
										</td>
									</c:if>
									<td class="nota" id="tdFaltas">
										
										<h:outputText value="#{itemNota.notaUnidade.faltas}" rendered="#{itemDisc.matricula.consolidada}" />
									
										<h:inputText styleClass="faltas" value="#{itemNota.notaUnidade.faltas}" title="Faltas #{regra.titulo}" size="1" maxlength="4" style="text-align: center;" 
										 	onkeydown="return(formataFalta(this, event))" onkeyup="atualizarFaltas#{itemDisc.matricula.id}();" immediate="true" rendered="#{!itemDisc.matricula.consolidada}" />
									</td>
								</c:if>					
							</c:if>
						</c:forEach>
						
					</c:if>
					
				</c:forEach>
				
				<td id="tdNota">
					<h:outputText value="#{itemDisc.mediaParcial}" id="medP_${itemDisc.matricula.id}" converter="convertNota" />
				</td>	
				<td id="tdNota">
					<h:outputText value="#{itemDisc.matricula.recuperacao}" converter="convertNota" rendered="#{itemDisc.matricula.consolidada}" />
					<h:inputText maxlength="4" onkeydown="return(formataValorNota(this, event, 1));" onkeyup="verificaNotaMaior(this);atualizarNotas#{ itemDisc.matricula.id }(true);"
						value="#{itemDisc.matricula.recuperacao}" size="3" disabled="#{!itemDisc.emRecuperacao}"
						id="recF_${itemDisc.matricula.id}" immediate="true" title="#{regra.titulo}"
						style="text-align: center; #{!itemDisc.emRecuperacao ? 'background-color:#F0F0F0' : 'background-color:#FFFFFF'}" rendered="#{!itemDisc.matricula.consolidada}"/>	
				</td>
				<td id="tdNota">
					<h:outputText value="#{itemDisc.matricula.mediaFinal}" id="medF_${itemDisc.matricula.id}" converter="convertNota"/>
				</td>			
				<td id="tdFaltasTotal" class="nota">
					<h:outputText value="#{itemDisc.matricula.numeroFaltas}" id="totalF_${itemDisc.matricula.id}"/>
				</td>			
				<td id="tdNota" class="nota" align="center">
					<h:outputText value="#{itemDisc.matricula.situacaoAbrev}" id="sitF_${itemDisc.matricula.id}"/>
				</td>
			</tr>
			
			<script>
				function atualizarNotas${ itemDisc.matricula.id }(rec){
					if (!rec)
						J(".atualizaNotas${ itemDisc.matricula.id }").click();
					else
						J(".atualizaNotasRec${ itemDisc.matricula.id }").click();
				}
				
				function atualizarFaltas${ itemDisc.matricula.id }(){
					J(".atualizaFaltas${ itemDisc.matricula.id }").click();
				}
			</script>
			
			<a4j:commandLink styleClass='atualizaNotas#{ itemDisc.matricula.id }' 
				oncomplete="atualizarRegraRecuperacao(#{itemDisc.matricula.id});"
				actionListener="#{consolidarDisciplinaMBean.calcularNota}" 
				reRender="medS1_#{itemDisc.matricula.id}, medS2_#{itemDisc.matricula.id}j_id_1, medS1R_#{itemDisc.matricula.id}, medS2R_#{itemDisc.matricula.id}j_id_1, medP_#{itemDisc.matricula.id}, recF_#{itemDisc.matricula.id}, medF_#{itemDisc.matricula.id}, sitF_#{itemDisc.matricula.id}, imgStatus_#{itemDisc.matricula.id}" 
				ajaxSingle="false" requestDelay="100">
				
				<f:attribute name="matricula" value="#{itemDisc}"/>			
				
			</a4j:commandLink>
			
			<a4j:commandLink styleClass='atualizaNotasRec#{ itemDisc.matricula.id }' 
				oncomplete="atualizarRegraRecuperacao(#{itemDisc.matricula.id});"
				actionListener="#{consolidarDisciplinaMBean.calcularNota}" 
				reRender="medS1_#{itemDisc.matricula.id}, medS2_#{itemDisc.matricula.id}j_id_1, medS1R_#{itemDisc.matricula.id}, medS2R_#{itemDisc.matricula.id}j_id_1, medP_#{itemDisc.matricula.id}, medF_#{itemDisc.matricula.id}, sitF_#{itemDisc.matricula.id}, imgStatus_#{itemDisc.matricula.id}" 
				ajaxSingle="false" requestDelay="100">
				
				<f:attribute name="matricula" value="#{itemDisc}"/>			
				
			</a4j:commandLink>
			
			<a4j:commandLink styleClass='atualizaFaltas#{ itemDisc.matricula.id }' actionListener="#{consolidarDisciplinaMBean.calcularFalta}" 
				reRender="sitF_#{itemDisc.matricula.id}, totalF_#{itemDisc.matricula.id}, imgStatus_#{itemDisc.matricula.id}" ajaxSingle="false" requestDelay="100">
				
				<f:attribute name="matricula" value="#{itemDisc}"/>
				
			</a4j:commandLink>
			
		</c:forEach>
		</tbody>
		
		</table>
	</div>
	
	<div class="opcoes">
	<input type="hidden" name="id" value="${idTurmaSerie}">
		<table align="right" width="20%">
		<tr>
			<td width="25%" align="center" valign="top"><h:commandButton action="#{consolidarDisciplinaMBean.salvar}" image="/img/consolidacao/disk_green.png" alt="Salvar" title="Salvar" rendered="#{!consolidarDisciplinaMBean.turma.consolidada}"/></td>
			<td width="25%" align="center" valign="top"><h:commandButton action="#{ consolidarDisciplinaMBean.confirmarParcial }" image="/img/consolidacao/disk_yellow.png" alt="Consolidação Parcial" title="Consolidação Parcial" onclick="if(!confirm('A consolidação parcial tem como objetivo consolidar as matrículas dos alunos aprovados por média. Após a operação, se houverem alunos em recuperação, a turma continuará aberta, permitindo a sua consolidação após a realização da prova de recuperação. Deseja continuar?')) return false;" rendered="#{!consolidarDisciplinaMBean.turma.consolidada}"/></td>
			<td width="25%" align="center" valign="top"><h:commandButton id="final" action="#{ consolidarDisciplinaMBean.confirmar }" image="/img/consolidacao/disk_blue_ok.png" alt="Finalizar (Consolidar)" title="Finalizar (Consolidar)" rendered="#{!consolidarDisciplinaMBean.turma.consolidada}"/></td>
			<%/*
				<td width="25%" align="center" valign="top">
					<h:commandButton action="#{consolidarDisciplinaMBean.selecionarTurma}" onclick="javascript: history.back();" image="/img/consolidacao/nav_left_red.png" alt="Voltar" title="Voltar" immediate="true"/>
				</td>
				<td width="25%" align="center" valign="top">
					<h:commandLink action="#" title="Imprimir" target="_blank">
						<h:graphicImage value="/img/consolidacao/printer.png"/>
					</h:commandLink>
				</td>
				<td width="25%" align="center" valign="top"><h:commandButton action="#" image="/img/consolidacao/publicar.png" alt="Salvar e Publicar" title="Salvar e Publicar"/></td>
				<td width="25%" align="center" valign="top"><h:commandButton action="#" image="/img/consolidacao/ocultar.png" alt="Salvar e Ocultar" title="Salvar e Ocultar"/></td>
			*/%>
		</tr>
		<tr>
		<!-- 
			<td width="25%" align="center" valign="top">
				<h:commandLink action="#{consolidarDisciplinaMBean.selecionarTurma}" value="Voltar" title="Voltar" immediate="true"/>
			</td>
		 -->	
			<td width="25%" align="center" valign="top"><h:commandLink action="#{consolidarDisciplinaMBean.salvarNotas}" value="Salvar" title="Salvar" rendered="#{!consolidarDisciplinaMBean.turma.consolidada}"/></td>
			<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarDisciplinaMBean.confirmarParcial }" value="Consolidação Parcial" title="Consolidação Parcial" rendered="#{!consolidarDisciplinaMBean.turma.consolidada}" onclick="if(!confirm('A consolidação parcial tem como objetivo consolidar as matrículas dos alunos aprovados por média. Após a operação, se houverem alunos em recuperação, a turma continuará aberta, permitindo a sua consolidação após a realização da prova de recuperação. Deseja continuar?')) return false;"/></td>
			<td width="25%" align="center" valign="top"><h:commandLink action="#{ consolidarDisciplinaMBean.confirmar }" value="Finalizar (Consolidar)" title="Finalizar (Consolidar)" rendered="#{!consolidarDisciplinaMBean.turma.consolidada}"/></td>
		</tr>
		</table>
	</div>
	
	<div class="legenda">
		<%/*
		<img src="${ ctx }/img/consolidacao/add.png"/><strong> - Desmembrar unidade em mais de uma avaliação</strong><br/>
		<img src="${ ctx }/img/consolidacao/delete.png"/><strong> - Remover avaliação</strong><br/>
		*/%>
		<img src="${ctx}/img/consolidacao/arrow_right.png"/><strong> - Transferir as faltas calculadas da lista de freqüência para o campo de faltas do aluno.</strong><br/>
		<img src="${ ctx }/img/consolidacao/situacao_apr.png"/><strong> - Aluno Aprovado</strong><br/>
		<img src="${ ctx }/img/consolidacao/situacao_rec.png"/><strong> - Aluno em Recuperação</strong><br/>
		<img src="${ ctx }/img/consolidacao/situacao_rep.png"/><strong> - Aluno Reprovado</strong>
	</div>
	
	<br/>
	<div class="botao" align="center">
	    <h:commandButton value="<< Voltar" action="#{consolidarDisciplinaMBean.voltar}" id="voltar" immediate="true"/>
	    <h:commandButton value="Cancelar" action="#{consolidarDisciplinaMBean.cancelar}" onclick="#{confirm}" id="cancelar" immediate="true"/>
	</div>

	<script type="text/javascript">

		/** Atualiza os campos de recuperação para verificar
		  é necessário digitar ou não a recuperação */
		function atualizarRegraRecuperacao(idmatricula) {

			/** Percorre todos os inputs que comeam com o id da matricula informada e 
				contenha "rec" (inputs de recuperação)*/
				//[id^="form\:nota_'+idmatricula+'"]
			J('input[id^="nota_'+idmatricula+'"][id*="rec"]').each(function(){

				/** executa a função declarada no a4j:jsFunction para renderizar o 
				 campo de recuperação da linha digitada */
				 J.globalEval( "atualizaRegra_" + J(this).attr("id") + "();" );
					
			});	

			return true;
		}			
	</script>	
	
	<style>
		.linhaPar .destaqueCinza {
			background:#D5E2F0;
		}
		
		.linhaImpar .destaqueCinza {
			background:#B4C6E2;
		}
	</style>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

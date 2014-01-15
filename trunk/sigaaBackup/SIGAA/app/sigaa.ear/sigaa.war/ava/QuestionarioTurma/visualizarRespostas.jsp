<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	
	<a4j:keepAlive beanName="questionarioTurma" />
	
	<h:form id="form">
	
	<fieldset>
	<legend>Questionários Submetidos</legend>
	
	<div class="infoAltRem">
		<c:if test="${turmaVirtual.docente }">
			<img src="/sigaa/ava/img/selecionado.gif">: Questionário Corrigido
			<img src="/sigaa/img/email_go.png">: Enviar Mensagem<br />
			<h:graphicImage value="/ava/img/corrigir.png" />: Visualizar Respostas / Corrigir Dissertativas
			<img src="/sigaa/ava/img/bin.png">: Apagar resposta
			<br/>
			<c:if test="${ questionarioTurma.questionario.tentativas > 1 }">
				<h:graphicImage value="/img/baixo.gif" style="overflow: visible;" />: Exibir as tentativas
				<h:graphicImage value="/img/cima.gif" style="overflow: visible;" />: Esconder as tentativas
			</c:if>	
		</c:if>
	</div>
	
	<div style="text-align: center; border: 1px solid #DEDEDE;margin:10px auto 10px auto;padding:10px;">
		Você está avaliando o questionário: <b><h:outputText value="#{questionarioTurma.questionario.titulo}" /></b>
	</div>
	
	<c:set var="respostas" value="null"/>
	<c:if test="${ not empty questionarioTurma.respostasConjunto && questionarioTurma.questionario.tentativas == 1 }">
	<table class="listing">
	
			<thead>
				<tr>
					<th style="width:20px;">&nbsp;</th>
					<th style="width:20px;">&nbsp;#</th>
					<th style="text-align:left;">Aluno</th>
					<th style="text-align:center;width:200px;">Data/Hora de envio</th>
					<th style="text-align:center;width:70px;">Finalizado</th>
					<th style="text-align:center;width:70px;">Acerto</th>
					<th style="width:20px;">&nbsp;</th>
					<th style="width:20px;">&nbsp;</th>
					<th style="width:20px;">&nbsp;</th>
				</tr>
			</thead>
	
			<tbody>
					<c:forEach items="#{ questionarioTurma.respostasConjunto }" var="r" varStatus="loop">
						<tr class='${ loop.index % 2 == 0 ? "even" : "odd" }' ${ !r.dissertativasPendentes ? "style='background-color: #FFFFAA'" : "" }>
							<td style="border-left: 1px solid #666666;width:0px;">
								<h:graphicImage value="/ava/img/selecionado.gif" rendered="#{ !r.dissertativasPendentes }" title="Respostas Corrigidas" />
							</td>
							<td style="text-align:right;">
								${loop.index+1}
							</td>
					
							<td>
								<h:outputText value="#{ r.usuarioEnvio.pessoa.nome }"/>
							</td>
							<td style="text-align:center;">
								<ufrn:format type="dataHora" valor="${ r.dataFinalizacao }"/>
							</td>
							<td style="text-align:center;">
								<c:if test="${r.finalizado}">
									Sim
								</c:if>
								<c:if test="${ not r.finalizado}">
									Não
								</c:if>
							</td>
							
							<td style="text-align:center;">
								<h:outputText value="#{ r.porcentagemString }" />
							</td>
							
							<td class="icon">
								<h:commandLink id="corrigir" action="#{questionarioTurma.preCorrigir}" title="Visualizar Respostas / Corrigir Dissertativas">
									<f:param name="id" value="#{r.id}" />
									<h:graphicImage value="/ava/img/corrigir.png" alt="Visualizar Respostas / Corrigir Dissertativas" />
		            			</h:commandLink>
		            		</td>
		            		
							<td class="icon">
								<a href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ r.usuarioEnvio.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
							</td>
			            	
							<td class="icon">
								<h:commandLink id="apagar" action="#{ questionarioTurma.removerRespostas }" onclick="#{confirmDelete}">
									<f:param name="idRespostas" value="#{ r.id }"/>
									<h:graphicImage value="/ava/img/bin.png" title="Apagar resposta" />
								</h:commandLink>
							</td>
						</tr>
						</c:forEach>
				
				</tbody>
		</table>
	</c:if>
	<c:if test="${ not empty questionarioTurma.questionario.conjuntos && questionarioTurma.questionario.tentativas > 1}">
		
		<div style="text-align: center; border: 1px solid #DEDEDE;margin:10px auto 10px auto;padding:10px;">
			O questionário está configurado para que o acerto do aluno 
			<c:if test="${ questionarioTurma.questionario.ultimaNota}">
				seja sua <b>última tentativa</b>.
			</c:if>	
			<c:if test="${ questionarioTurma.questionario.notaMaisAlta}">
				seja sua <b>melhor tentativa</b>.
			</c:if>	
			<c:if test="${ questionarioTurma.questionario.mediasDasNotas}">
				seja a <b>média de suas tentativas</b>.
			</c:if>		
			</b>
		</div>
				
		<table class="listing">		
			<thead>
				<tr>	
					<th style="width:6%;">&nbsp;</th>
					<th style="width:20px;">&nbsp;#</th>
					<th style="text-align:left;width:64%">Aluno</th>
					<th style="text-align:center;">Acerto</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			
			<tbody>
			<c:forEach items="#{ questionarioTurma.questionario.conjuntos }" var="c" varStatus="loop">
				<tr class='${ loop.index % 2 == 0 ? "even" : "odd" }' ${ !c.dissertativasPendentes ? "style='background-color: #FFFFAA'" : "" }>
					<td style="border-left: 1px solid #666666;width:0px;">
						<h:graphicImage value="/ava/img/selecionado.gif" rendered="#{ !c.dissertativasPendentes }" title="Respostas Corrigidas" />
					</td>
			
					<td style="text-align:right;">
						${loop.index+1}
					</td>
			
					<td style="width:70%;">
						<h:outputText value="#{ c.usuarioEnvio.pessoa.nome }"/>
					</td>
					
					<td style="text-align:center;">
						<h:outputText value="#{ c.porcentagemString }" />
					</td>
					
					<td class="icon">
					<img src="/sigaa/img/baixo.gif" title="Exibir as tentativas" onclick="exibirTentativas(this,${c.id});">
	           		</td>
	           		
					<td class="icon">
						<a href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ r.usuario.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
					</td>
	            	
					<td class="icon">
						<h:commandLink id="apagarTodas" action="#{ questionarioTurma.removerConjuntoRespostas }" onclick="#{confirmDelete}">
							<f:param name="idConjunto" value="#{ c.id }"/>										
							<h:graphicImage value="/ava/img/bin.png" title="Apagar todas as respostas" />
						</h:commandLink>
					</td>
				</tr>
			
				<tr id='idTentativas_${c.id}' class='${ loop.index % 2 == 0 ? "even" : "odd" }' ${ !c.dissertativasPendentes ? "style='display:none;background-color: #FFFFAA'" : "style='display:none;'" }>
				<td style="border-left: 1px solid #666666;width:0px;" colspan="7">
				<table  class="listing" style="width:95%;margin-left:40px;margin-top:10px;">
					<thead>
						<tr>
							<th style="width:20px;">&nbsp;</th>
							<th style="text-align:left;">Tentativas</th>
							<th style="text-align:center;width:200px;">Data/Hora de envio</th>
							<th style="text-align:center;width:70px;">Finalizado</th>
							<th style="text-align:center;width:70px;">Acerto</th>
							<th style="width:20px;">&nbsp;</th>
							<th style="width:20px;">&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{ c.respostas }" var="r" varStatus="loop">
						<tr ${ !r.dissertativasPendentes ? "style='background-color: #FFFFAA'" : "style=''" }>
							<td style="border-left: 1px solid #666666;width:0px;">
								<h:graphicImage value="/ava/img/selecionado.gif" rendered="#{ !r.dissertativasPendentes }" title="Respostas Corrigidas" />
							</td>
					
							<td>
								Tentativa ${ loop.index+1 }
							</td>
							<td style="text-align:center;">
								<ufrn:format type="dataHora" valor="${ r.dataFinalizacao }"/>
							</td>
							<td style="text-align:center;">
								<c:if test="${r.finalizado}">
									Sim
								</c:if>
								<c:if test="${ not r.finalizado}">
									Não
								</c:if>
							</td>
							
							<td style="text-align:center;">
								<h:outputText value="#{ r.porcentagemString }" />
							</td>
							
							<td class="icon">
								<h:commandLink id="corrigir" action="#{questionarioTurma.preCorrigir}" title="Visualizar Respostas / Corrigir Dissertativas">
									<f:param name="id" value="#{r.id}" />
									<h:graphicImage value="/ava/img/corrigir.png" alt="Visualizar Respostas / Corrigir Dissertativas" />
		            			</h:commandLink>
		            		</td>
			            	
							<td class="icon">
								<h:commandLink id="apagar" action="#{ questionarioTurma.removerRespostas }" onclick="#{confirmDelete}">
									<f:param name="idRespostas" value="#{ r.id }"/>
									<h:graphicImage value="/ava/img/bin.png" title="Apagar resposta" />
								</h:commandLink>
							</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>	
			</td></tr>
			
		</c:forEach>	
		</tbody>
	</table>			
			
	</c:if>
	
	<c:if test="${ empty questionarioTurma.conjuntoRespostas }">
		<br>
		<center><i>Nenhum aluno respondeu a este questionário.</i></center>
	</c:if>
			
	<div class="botoes">
		<div style="margin-right:40%;" class="other-actions">
			<h:commandButton id="voltar" action="#{ questionarioTurma.listarQuestionariosDocente }" value="<< Voltar aos Questionários"/> 
		</div>
	</div>
		
	</fieldset>
	
	</h:form>
<script type="text/javascript">

	function exibirTentativas (img,idTentativas) {

		var id = "idTentativas_" + idTentativas
		var elem = document.getElementById(id);

		if (elem.style.display == "none") {
			elem.style.display = "";
			img.src="/sigaa/img/cima.gif"
			img.title ="Esconder as tentativas"	
		}		
		else {
			elem.style.display = "none";
			img.src="/sigaa/img/baixo.gif"
			img.title ="Exibir as tentativas"
		}		
	}
</script>
</f:view>
<%@include file="/ava/rodape.jsp" %>
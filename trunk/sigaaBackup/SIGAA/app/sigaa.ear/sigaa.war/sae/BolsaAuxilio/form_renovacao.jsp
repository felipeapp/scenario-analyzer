<%@include file="/ensino/notificacoes_academicas/cabecalho.jsp"%>

<h2> Renovação Bolsas </h2>

<jwr:style src="/css/ensino/notificacoes.css" media="all" />
<a4j:keepAlive beanName="renovacaoBolsaAuxilioMBean" />
<style>
div.logon h3{ font-size: 12px !important;padding-right:15px;}
.confirmaSenha { float: left !important; }
</style>

<f:view>
	<h:form>

	<img src="${ctx}/img/notificacao/imagemNotificacoes.png" style="padding-left:20px;float:left"/>
	
	<div class="intro">
			
			<div class="textos">
			
				<img src="${ctx}/img/warning.gif" style="vertical-align:middle;"/>
				<b>Caro discente, <br/> <br /></b>
					Detectamos que você possui as seguintes bolsas passiveis de renovação: 
				<br />
				<br />
				<br />
				
				<div style="margin-left:30%">
					<c:forEach items="#{renovacaoBolsaAuxilioMBean.bolsas}" var="b" varStatus="loop">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>
								<b>
									${ b.tipoBolsaAuxilio.denominacao } / ${ b.situacaoBolsa.denominacao }
								</b>
							</td>
						</tr>
					</c:forEach>
				</div>
				
				<br />
				<br />
				
				Para realizar sua matricula você precisa solicitar renovação da sua bolsa. <br />
				
				<br /> <b style="color: red;" >Mas Atenção!! </b> <br /><br />
				
				<p>	
					A solicitação de renovação não implica na renovação da bolsa. <br />
					Sua solicitação será enviada para a PROAE e será analisada no período divulgado em edital. </p><br /> 
				
					Somente após a avaliação da documentação comprobatória da situação de cada aluno é que cada benefício será definitivamente
					deferido ou indeferido.
				
				<br />
				<br />
				
				Caso você não deseje renovar sua bolsa, clique em Não solicitar renovação e sua bolsa será <i style='text-decoration: underline;'>CANCELADA</i> ao final do semestre de vigência da mesma.

			</div>

			<div class="botao left">
				<h:commandLink id="btnResponderQuestionario" action="#{ renovacaoBolsaAuxilioMBean.iniciarRenovacaoBolsaAuxilio }">
					<span>Solicitar Renovação</span>
 				</h:commandLink>
			</div>
			<div class="botao_depois left">
				<h:commandLink id="btnNaoResponderContinuar" action="#{ renovacaoBolsaAuxilioMBean.naoRenovacaoBolsaAuxilio }" 
					onclick="if(confirm('A não solicitação da renovação implicará na finalização do auxílio, gostaria de continuar?')) return true; else return false;">
           			<span>Não Solicitar Renovação</span>
 				</h:commandLink>
			</div>
			<br clear="all"/>
	</div>
	
	</h:form>
	
</f:view>	

<%@include file="/ensino/notificacoes_academicas/rodape.jsp"%>
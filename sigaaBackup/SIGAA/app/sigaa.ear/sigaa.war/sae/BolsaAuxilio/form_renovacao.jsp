<%@include file="/ensino/notificacoes_academicas/cabecalho.jsp"%>

<h2> Renova��o Bolsas </h2>

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
					Detectamos que voc� possui as seguintes bolsas passiveis de renova��o: 
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
				
				Para realizar sua matricula voc� precisa solicitar renova��o da sua bolsa. <br />
				
				<br /> <b style="color: red;" >Mas Aten��o!! </b> <br /><br />
				
				<p>	
					A solicita��o de renova��o n�o implica na renova��o da bolsa. <br />
					Sua solicita��o ser� enviada para a PROAE e ser� analisada no per�odo divulgado em edital. </p><br /> 
				
					Somente ap�s a avalia��o da documenta��o comprobat�ria da situa��o de cada aluno � que cada benef�cio ser� definitivamente
					deferido ou indeferido.
				
				<br />
				<br />
				
				Caso voc� n�o deseje renovar sua bolsa, clique em N�o solicitar renova��o e sua bolsa ser� <i style='text-decoration: underline;'>CANCELADA</i> ao final do semestre de vig�ncia da mesma.

			</div>

			<div class="botao left">
				<h:commandLink id="btnResponderQuestionario" action="#{ renovacaoBolsaAuxilioMBean.iniciarRenovacaoBolsaAuxilio }">
					<span>Solicitar Renova��o</span>
 				</h:commandLink>
			</div>
			<div class="botao_depois left">
				<h:commandLink id="btnNaoResponderContinuar" action="#{ renovacaoBolsaAuxilioMBean.naoRenovacaoBolsaAuxilio }" 
					onclick="if(confirm('A n�o solicita��o da renova��o implicar� na finaliza��o do aux�lio, gostaria de continuar?')) return true; else return false;">
           			<span>N�o Solicitar Renova��o</span>
 				</h:commandLink>
			</div>
			<br clear="all"/>
	</div>
	
	</h:form>
	
</f:view>	

<%@include file="/ensino/notificacoes_academicas/rodape.jsp"%>
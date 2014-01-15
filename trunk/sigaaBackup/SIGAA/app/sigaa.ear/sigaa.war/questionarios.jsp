<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<link href="/shared/css/questionario.css" rel="stylesheet" type="text/css" />
<style>
	#menu-usuario{display:none !important;}
</style>
<f:view>

	<h:form prependId="false">
		<h2>Solicitação de Resposta ao Questionário</h2>
		<div class="intro">
	
			<div class="textos">
				<p class="textos"><strMódulosong>Prezado usuário,</strong></p>
				<p class="textos">Há um questionário disponível com o título: <strong>${ verTelaAvisoLogonMBean.questionarioAtual.titulo }</strong> onde seu cadastro se insere no público alvo.</p> 
				<p class="textos"><strong>Maiores Informações:</strong></p>
				<p class="textos">${ verTelaAvisoLogonMBean.questionarioAtual.descricao }</p>
				<p class="textos">Por favor, selecione uma das opções abaixo:</p>
			</div>
			<div class="botao left">
				<a4j:commandLink id="btnResponderQuestionario" 
					action="#{verTelaAvisoLogonMBean.proxima}" onclick="popup();" >    
					<span>Responder Questionário</span>
				</a4j:commandLink>
			</div>
			<div class="botao_depois left">
				<a4j:commandLink id="btnNaoResponderContinuar">  
               		<rich:componentControl for="view" attachTo="btnNaoResponderContinuar" operation="show" event="onclick" />  
               		<span>Não responder e continuar</span>
				</a4j:commandLink> 
			</div>
			<br clear="all"/>
		</div>
		
		<%-- 
			Janela informando se o usuário deseja ser lembrado do questionário em um acesso posterior.
			Utilizada somente quando o botão Não respodenr, continuar for selecionado.
		  --%>		
		<rich:modalPanel id="view" autosized="true" minHeight="110" minWidth="400" width="400" >  
	        <f:facet name="header">  
	            <h:panelGroup>
	                <h:outputText value="Solicitação de Resposta do Questionário Aplicado"></h:outputText>
	            </h:panelGroup>
	        </f:facet>
	        <f:facet name="controls">
	            <h:panelGroup>
		            <h:outputLink value="#" id="btn1" >  
		   		         <h:graphicImage value="/img/close.png"  style="margin-left:5px; cursor:pointer; border: none" />  
		                 <rich:componentControl for="view" attachTo="btn1" operation="hide" event="onclick" />  
		            </h:outputLink>
	            </h:panelGroup>
	        </f:facet>
	       	<table width="100%">
	        	<tr>
	        		<td>
	        			Deseja ser lembrado mais tarde sobre o questionário com o título: 
	        			<strong>${ verTelaAvisoLogonMBean.questionarioAtual.titulo }</strong>?
	        		</td>
	        	</tr>
	        	<tr>
	        		<td>
	        			<br/>
	        			<a4j:commandButton value="Sim" action="#{verTelaAvisoLogonMBean.proxima}" id="btnSimLembrarQuestionario">
	        			  	<rich:componentControl for="view" attachTo="btnSimLembrarQuestionario" operation="hide" event="onclick" />
	        			</a4j:commandButton>
						<a4j:commandButton value="Não" id="btnNaoLmebrarQuestionario" action="#{verTelaAvisoLogonMBean.ignorarQuestionario}">
							 <rich:componentControl for="view" attachTo="btnNaoLmebrarQuestionario" operation="hide" event="onclick" />
						</a4j:commandButton> 
	        		</td>
	        	</tr>
		 	 </table>
	    </rich:modalPanel>
				
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>

<script>
function popup() {
	var pageURL = '${ pageContext.request.contextPath }/responderQuestionario.jsf?identificador=${ verTelaAvisoLogonMBean.questionarioAtual.identificador }';
	var w = 700, h = 500;
	var left = (screen.width/2)-(w/2);
	var top = (screen.height/2)-(h/2);
	var targetWin = window.open (pageURL, 'QuestionarioAplicado', 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=no, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);
} 			
</script>
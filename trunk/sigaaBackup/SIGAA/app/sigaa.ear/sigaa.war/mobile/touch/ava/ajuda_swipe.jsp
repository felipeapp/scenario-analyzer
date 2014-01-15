<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-ajuda-swipe" data-theme="b" data-add-back-btn="true">
		<div data-role="header" data-theme="b">
			<h1>Ajuda</h1>
		</div>
		
		<div data-role="content">
				<%@include file="/mobile/touch/include/mensagens.jsp"%>				
	     		<h:form id="form-ajuda-swipe">
	     			<table align="center">
						<tr>
							<th>
	     						<h:graphicImage value="/mobile/touch/img/swipe_left.PNG"/>
	     					</th>
	     					<th>
	     						É possível mudar rapidamente o tópico de aula visualizado deslizando o dedo sobre a tela para a
	     						esquerda ou para a direita, dependendo de qual tópico de aula você deseje ver. Experimente!
	     					</th>		
	     					<th>
	     						<h:graphicImage value="/mobile/touch/img/swipe_right.PNG"/>
	     					</th>
	     				</tr>
	     			</table>     			
			  	</h:form>
		</div>

     </div>
</f:view>
<%@include file="../include/rodape.jsp"%>
			

<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp" %>

	<script type="text/javascript">
		JAWR.loader.script('/javascript/jquery/jquery.js');
	</script>
	
	<f:view>
		<a4j:keepAlive beanName="categoriaPerguntaQuestionarioTurma" />
		<h:form>
			
			<c:forEach var="categorias" items="#{categoriaPerguntaQuestionarioTurma.listaCategoriasRelatorio}" varStatus="status">
				<br/>	
				<%@include file="/ava/QuestionarioTurma/_relatorioQuestoes.jsp"%>
			</c:forEach>	

			<div class="categoriasCompartilhadas">
				<c:forEach var="categorias" items="#{categoriaPerguntaQuestionarioTurma.listaCategoriasCompartilhadasRelatorio}" varStatus="status">
					<br/>	
					<%@include file="/ava/QuestionarioTurma/_relatorioQuestoes.jsp"%>
				</c:forEach>	
			</div>
				
		</h:form>
				<div align="center" class="naoImprimir" style="color: blue">
					<b><label id="mostrarLabel" for="mostrar">Imprimir Mostrando Respostas:</label></b>
					<h:selectBooleanCheckbox id="mostrar" onclick="mostrarRespostas(this)" value="#{true}" />
				</div>
				<div align="center" class="naoImprimir" style="color: blue">
					<b><label id="mostrarFeedbackLabel" for="mostrarFeedback">Imprimir Mostrando Feedbacks:</label></b>
					<h:selectBooleanCheckbox id="mostrarFeedback" onclick="mostrarFeedback(this)" value="#{false}" />
				</div>
				<div align="center" class="naoImprimir" style="color: blue">
					<b><label id="mostrarCategoriasCompartilhadasLabel" for="mostrarCategoriasCompartilhadas">Imprimir Mostrando Categorias Compartilhadas:</label></b>
					<h:selectBooleanCheckbox id="mostrarCategoriasCompartilhadas" onclick="mostrarCategoriasCompartilhadas(this)" value="#{false}" />
				</div>
	
	</f:view>
	
	<script type="text/javascript">
	
	function mostrarRespostas(selected) {
		
		
		var J  = jQuery.noConflict();
		var gabaritos = J(".gabarito");
		var semGabarito = J(".semGabarito");

		if (selected == null || selected.checked) {
			gabaritos.each(function() {
					J(this).show();
				} );

			semGabarito.each(function() {
				J(this).hide();
			
			} );
		}
		else {
			gabaritos.each(function() {
				J(this).hide();
			
			} );

			semGabarito.each(function() {
				J(this).show();
			} );

			Acerto			
		}
	}

	function mostrarFeedback(selected) {
		
		
		var J  = jQuery.noConflict();
		var feedbackAcerto = J(".feedbackAcerto");
		var feedbackErro = J(".feedbackErro");

		if (selected == null || !selected.checked) {			
			feedbackAcerto.each(function() {J(this).hide();	} );
			feedbackErro.each(function() {J(this).hide();	} );
		}
		else {	
			feedbackAcerto.each(function() {J(this).show();	} );
			feedbackErro.each(function() {J(this).show();	} );			
		}
	}
	
	function mostrarCategoriasCompartilhadas(selected) {
		
		
		var J  = jQuery.noConflict();
		var feedbackAcerto = J(".categoriasCompartilhadas");

		if (selected == null || !selected.checked) 		
			feedbackAcerto.each(function() {J(this).hide();	} );
		else 
			feedbackAcerto.each(function() {J(this).show();	} );

	}

	
	mostrarRespostas(null);
	mostrarFeedback(null);
	mostrarCategoriasCompartilhadas(null);
	
	</script>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>
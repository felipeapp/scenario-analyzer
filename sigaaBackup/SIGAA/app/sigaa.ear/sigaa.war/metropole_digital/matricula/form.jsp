<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>


<script type="text/javascript">

	var checkflag = "false";

	function selectAllCheckBox() {
	    var div = document.getElementById('form');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    if (checkflag == "false") {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = true; }
	            }
	            checkflag = "true";
	    } else {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = false; }
	            }
	            checkflag = "false";
	    }
	}

</script>


<f:view>
	<a4j:keepAlive beanName="matriculaDiscenteSemTurma"/>	
	<h2><ufrn:subSistema /> > Matricular Discentes sem Turma </h2>
	
	<h:form>
		<c:if test="${(not empty matriculaDiscenteSemTurma.turmaEntradaSelecionada) && (not empty matriculaDiscenteSemTurma.opcaoPoloSelecionada)}">		
			<p align="center"><h2 align="center">TURMA: ${matriculaDiscenteSemTurma.turmaEntradaSelecionada.anoReferencia}.${matriculaDiscenteSemTurma.turmaEntradaSelecionada.periodoReferencia} - ${matriculaDiscenteSemTurma.turmaEntradaSelecionada.especializacao.descricao} - ${matriculaDiscenteSemTurma.turmaEntradaSelecionada.cursoTecnico.nome}
			<br />OPÇÃO PÓLO GRUPO: ${matriculaDiscenteSemTurma.turmaEntradaSelecionada.opcaoPoloGrupo.descricao}</h2></p>
			
			<rich:dataTable value="#{ matriculaDiscenteSemTurma.listaDiscentes }" var="discente" width="100%" rowKeyVar="c">
	
				<f:facet name="caption"><f:verbatim>Discentes Encontrados</f:verbatim></f:facet>
	
				<rich:column styleClass="#{c % 2 == 0 ? 'linhaPar': 'linhaImpar' }"  width="20">
					<f:facet name="header">
						<f:verbatim>
						<a href="#" onclick="selectAllCheckBox();">Todos</a>
						</f:verbatim>
					</f:facet>
					<h:selectBooleanCheckbox value="#{ discente.matricular }"/>
				</rich:column>
			
				<rich:column styleClass="#{c % 2 == 0 ? 'linhaPar': 'linhaImpar'}">
					<f:facet name="header"><f:verbatim>Matrícula</f:verbatim></f:facet>
					<h:outputText value="#{ discente.matricula }"/>
				</rich:column>
			
				<rich:column styleClass="#{c % 2 == 0 ? 'linhaPar': 'linhaImpar'}">
					<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
					<h:outputText value="#{ discente.pessoa.nome }"/>
				</rich:column>
			
			</rich:dataTable>
			
		</c:if>
		
		
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>
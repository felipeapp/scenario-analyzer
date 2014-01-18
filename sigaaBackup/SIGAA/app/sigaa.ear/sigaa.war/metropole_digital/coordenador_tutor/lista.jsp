<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">

	

	function unselectAllCheckBoxTodos() {
		
	    var div = document.getElementById('busca');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    
        for ( i = 0; i < e.length ; i++) {
                if (e[i].type == "checkbox"){
                	if(e[i].id != "busca:chkAtivos") {
                		e[i].checked = false;	
                	}	
               	}     
        }
	    
        
       
	}
	
	function desmarcarListarTodos() {
		
	    var div = document.getElementById('busca');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    
        for ( i = 0; i < e.length ; i++) {
                if (e[i].type == "checkbox"){
                	if(e[i].id == "busca:chkAtivos") {
                		e[i].checked = false;	
                	}	
               	}     
        }
	    
        
       
	}
	
	
	function desmarcarCheckTodosAndInativos() {
		
	    var div = document.getElementById('busca');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	     
	    
	    if(document.getElementById('busca:chkInativos').checked == false) {
	        for ( i = 0; i < e.length ; i++) {
	                if (e[i].type == "checkbox"){
	                	if(e[i].id == "busca:chkAtivos") {
	                		e[i].checked = true;	
	                	}	
	               	}     
	        }
	    } 
	    
	  
	}

</script>

<f:view>
	<h:form id="busca">
		<a4j:keepAlive beanName="coordenadorTutorIMD"/>
		
		<h2><ufrn:subSistema /> > Listagem de Coordenador de Tutores do IMD</h2>
		
	
		<h:outputText value="#{coordenadorTutorIMD.create}" />
	
	
		<table class="formulario" style="width:50%;">
		  <caption>Informe os critérios da busca</caption>
	 		<tbody>
	 		
	 			<tr>
	 				<td width="3%"> <h:selectBooleanCheckbox value="#{ coordenadorTutorIMD.buscaNome }" id="chkNome" onclick="desmarcarListarTodos();"/> </td>
					<td width="5%;"><label for="busca:chkNome">Nome: </label></td>
					<td><h:inputText value="#{coordenadorTutorIMD.nomePessoaBusca}" size="60"  onfocus="marcaCheckBox('busca:chkNome'); desmarcarListarTodos();" /> </td>
				</tr>
				
				<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{ coordenadorTutorIMD.buscaCpf }" id="chkCpf" onclick="desmarcarListarTodos();"/> </td>
					<td width="5%;"><label for="busca:chkCpf">CPF: </label></td>
					<td>
						<h:inputText value="#{ coordenadorTutorIMD.cpfPessoaBusca }" size="14" maxlength="14"
							onkeypress="return formataCPF(this, event, null);" id="cpfBusca"  onfocus="marcaCheckBox('busca:chkCpf'); desmarcarListarTodos();" >
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>
				
				<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{ coordenadorTutorIMD.buscaListarTodos }" id="chkAtivos" onclick="unselectAllCheckBoxTodos();"/> </td>
					<td colspan ="2" width="10%"><label for="busca:chkAtivos">Listar Todos</label></td>
				</tr>
				
		    </tbody>
		    <tfoot>
			    <tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{coordenadorTutorIMD.buscarCoordenador}" id="buscar" />
						<h:commandButton value="Cancelar" action="#{coordenadorTutorIMD.cancelarBuscaPessoa}"/>
					</td>
			    </tr>
			</tfoot>
		</table>
	</h:form>
	
	<br /><br />

	<c:if test="${not empty coordenadorTutorIMD.listaCoordenadores}">
		
		<div class="infoAltRem">
		  	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		</div>

		<table class=listagem style="width:100%">
			<caption class="listagem">Lista de Coordenadores de Pólo do IMD (${fn:length(coordenadorTutorIMD.listaCoordenadores)})</caption>
			<thead>
				<tr>
					<td>Nome</td>
					<td>CPF</td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{coordenadorTutorIMD.listaCoordenadores}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				
					<td>${item.pessoa.nome}</td>
					<td><ufrn:format type="cpf_cnpj" valor="${item.pessoa.cpf_cnpj}" /></td>					
					<td width=25>
						<h:commandLink action="#{coordenadorTutorIMD.remover}"  onclick="#{confirmDelete}">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</h:form>
		</table>
		<br />
		
	</c:if>
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

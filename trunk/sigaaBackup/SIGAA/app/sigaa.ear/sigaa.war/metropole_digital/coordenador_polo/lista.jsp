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
		<a4j:keepAlive beanName="coordenadorPoloIMD"/>
		
		<h2><ufrn:subSistema /> > Listagem de Coordenador de Pólo do IMD</h2>
		
	
		<h:outputText value="#{coordenadorPoloIMD.create}" />
	
	
		<table class="formulario" style="width:50%;">
		  <caption>Informe os critérios da busca</caption>
	 		<tbody>
	 		
	 			<tr>
	 				<td width="3%"> <h:selectBooleanCheckbox value="#{ coordenadorPoloIMD.buscaNome }" id="chkNome" onclick="desmarcarListarTodos();"/> </td>
					<td width="5%;"><label for="busca:chkNome">Nome: </label></td>
					<td><h:inputText value="#{coordenadorPoloIMD.nomePessoaBusca}" size="60"  onfocus="marcaCheckBox('busca:chkNome'); desmarcarListarTodos();" /> </td>
				</tr>
				
				<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{ coordenadorPoloIMD.buscaCpf }" id="chkCpf" onclick="desmarcarListarTodos();"/> </td>
					<td width="5%;"><label for="busca:chkCpf">CPF: </label></td>
					<td>
						<h:inputText value="#{ coordenadorPoloIMD.cpfPessoaBusca }" size="14" maxlength="14"
							onkeypress="return formataCPF(this, event, null);" id="cpfBusca"  onfocus="marcaCheckBox('busca:chkCpf'); desmarcarListarTodos();" >
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>
				
				<!--Lista de Pólos -->
				<tr>
					
					
					<td width="3%"> <h:selectBooleanCheckbox value="#{ coordenadorPoloIMD.buscaPolo }" id="chkPolo" onclick="desmarcarListarTodos();"/> </td>
					<td width="5%;"><label for="busca:chkPolo">Pólo: </label></td>
					<td>	
						<h:selectOneMenu value="#{ coordenadorPoloIMD.idPolo }" id="selectPolos"  onfocus="marcaCheckBox('busca:chkPolo'); desmarcarListarTodos();" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ coordenadorPoloIMD.polosCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{ coordenadorPoloIMD.buscaListarTodos }" id="chkAtivos" onclick="unselectAllCheckBoxTodos();"/> </td>
					<td colspan ="2" width="10%"><label for="busca:chkAtivos">Listar Todos</label></td>
				</tr>
				
		    </tbody>
		    <tfoot>
			    <tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{coordenadorPoloIMD.buscarCoordenador}" id="buscar" />
						<h:commandButton value="Cancelar" action="#{coordenadorPoloIMD.cancelarBuscaPessoa}"/>
					</td>
			    </tr>
			</tfoot>
		</table>
	</h:form>
	
	<br /><br />

	<c:if test="${not empty coordenadorPoloIMD.listaCoordenadores}">
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
		  	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		</div>

		<table class=listagem style="width:100%">
			<caption class="listagem">Lista de Coordenadores de Pólo do IMD (${fn:length(coordenadorPoloIMD.listaCoordenadores)})</caption>
			<thead>
				<tr>
					<td>Nome</td>
					<td>CPF</td>
					<td>Pólo</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{coordenadorPoloIMD.listaCoordenadores}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				
					<td>${item.pessoa.nome}</td>
					<td><ufrn:format type="cpf_cnpj" valor="${item.pessoa.cpf_cnpj}" /></td>
					<td>${item.polo.descricao}</td>
					<td width=20>
						<h:commandLink action="#{coordenadorPoloIMD.atualizar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>						
					<td width=25>
						<h:commandLink action="#{coordenadorPoloIMD.remover}"  onclick="#{confirmDelete}">
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

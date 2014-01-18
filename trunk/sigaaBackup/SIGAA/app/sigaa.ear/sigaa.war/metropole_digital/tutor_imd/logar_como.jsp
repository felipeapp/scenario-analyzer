<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">

	

	function unselectAllCheckBoxTodos() {
		
	    var div = document.getElementById('busca');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    if(document.getElementById('busca:chkAtivos').checked == true) {
	        for ( i = 0; i < e.length ; i++) {
	                if (e[i].type == "checkbox"){
	                	if(e[i].id == "busca:chkInativos") {
	                		e[i].checked = false;	
	                	}	
	               	}     
	        }
	    }
        
       
	}
	
	function unselectAllCheckBoxToInativos() {
		
	    var div = document.getElementById('busca');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    if(document.getElementById('busca:chkInativos').checked == true) {
	        for ( i = 0; i < e.length ; i++) {
	                if (e[i].type == "checkbox"){
	                	if(e[i].id == "busca:chkAtivos") {
	                		e[i].checked = false;	
	                	}	
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
		<a4j:keepAlive beanName="tutorIMD"/>
		
		<h2><ufrn:subSistema /> > Listagem de Tutor do IMD</h2>
		
	
		<h:outputText value="#{tutorIMD.create}" />
	
	
		<table class="formulario" style="width:50%;">
		  <caption>Informe os critérios da busca</caption>
	 		<tbody>
	 		
	 			<tr>
	 				<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorIMD.buscaNome }" id="chkNome" onclick=""/> </td>
					<td width="5%;"><label for="busca:chkNome">Nome: </label></td>
					<td><h:inputText value="#{tutorIMD.nomeTutorBusca}" size="60"  onfocus="marcaCheckBox('busca:chkNome');" /> </td>
				</tr>
				
				<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorIMD.buscaCpf }" id="chkCpf" onclick=""/> </td>
					<td width="5%;"><label for="busca:chkCpf">CPF: </label></td>
					<td>
						<h:inputText value="#{ tutorIMD.cpfTutorBusca }" size="14" maxlength="14"
							onkeypress="return formataCPF(this, event, null);" id="cpfBusca"  onfocus="marcaCheckBox('busca:chkCpf');" >
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
					</td>
				</tr>
				
				<!--Lista de Pólos -->
				<tr>
					
					
					<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorIMD.buscaPolo }" id="chkPolo" onclick=""/> </td>
					<td width="5%;"><label for="busca:chkPolo">Pólo: </label></td>
					<td>	
						<h:selectOneMenu value="#{ tutorIMD.idPoloTutorBusca }" id="selectPolos"  onfocus="marcaCheckBox('busca:chkPolo');" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{ poloBean.allCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorIMD.buscaListarTodos }" id="chkAtivos" onclick="unselectAllCheckBoxTodos();"/> </td>
					<td colspan ="2" width="10%"><label for="busca:chkAtivos">Somente Tutores Ativos</label></td>
				</tr>
				
				<!--<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{ tutorIMD.buscaInativos }" id="chkInativos" onclick="unselectAllCheckBoxToInativos();"/> </td>
					<td colspan ="2" width="10%"><label for="busca:chkInativos">Somente Tutores Inativos</label></td>
				</tr>-->
				
		    </tbody>
		    <tfoot>
			    <tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{tutorIMD.buscarTutor}" id="buscar" />
						<h:commandButton value="Cancelar" action="#{tutorIMD.cancelarBuscaPessoa}"/>
					</td>
			    </tr>
			</tfoot>
		</table>
	</h:form>
	
	<br /><br />

	<c:if test="${not empty tutorIMD.listaTutores}">
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/user.png"style="overflow: visible;"/>: Logar como
		</div>

		<table class=listagem style="width:100%">
			<caption class="listagem">Lista de Tutor do IMD</caption>
			<thead>
				<tr>
					<td>Nome</td>
					<td>CPF</td>
					<td>Pólo</td>
					<td>Data início</td>
					<td>Data fim</td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{tutorIMD.listaTutores}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				
					<td>${item.pessoa.nome}</td>
					<td><ufrn:format type="cpf_cnpj" valor="${item.pessoa.cpf_cnpj}" /></td>
					<td>${item.polo.descricao}</td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataInicio}"/></td>
					<td><fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataFim}"/></td>
					
					<td width=20>
						<h:commandLink action="#{logarComoTutorIMD.logar}" >
							<h:graphicImage value="/img/user.png" style="overflow: visible;" title="Logar como" alt="Logar como"/>
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


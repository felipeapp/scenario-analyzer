<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Coordenação Curso</h2>
	
	<%@include file="include/_operacao.jsp"%>
	
<h:form id="form">
  <table class=formulario width="100%">
   <caption class="listagem">Dados da Coordenação do Curso</caption>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
		 <caption>Dados Básicos do Coordenador</caption>
			<tr>
				<th width="20%" class="obrigatorio">Coordenador:</th>
				<td>
					<h:inputText value="#{cursoLatoMBean.obj.coordenador.servidor.pessoa.nome}" id="nomeCoord" size="59"/>
					<rich:suggestionbox for="nomeCoord" width="450" height="100" minChars="3" id="suggestionNomeCoord" 
						suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_coord" 
						fetchValue="#{_coord.pessoa.nome}">
					 
						<h:column>
							<h:outputText value="#{_coord.pessoa.nome}" />
						</h:column>
					 
					    <f:param name="apenasAtivos" value="true" />
				        <a4j:support event="onselect" actionListener="#{ cursoLatoMBean.carregaInformacao }" immediate="true">
				        	   <f:param name="apenasAtivos" value="true" />
				               <f:attribute name="funcaoCoordenador" value="true"/>
				               <f:attribute name="servCoord" value="#{_coord}"/>
					    </a4j:support>
					</rich:suggestionbox>
				</td>
			</tr>
	  	       <tr>
			    	<th class="obrigatorio">Email de Contato:</th>
					<td>
						<h:inputText value="#{cursoLatoMBean.obj.coordenador.emailContato}" size="50" maxlength="100" id="emailContato" />
					</td>
			   </tr>
  	       <tr>
		    	<th class="obrigatorio">Telefone de Contato:</th>
				<td>
					<h:inputText value="#{cursoLatoMBean.obj.coordenador.telefoneContato1}" size="10" maxlength="9" id="telefoneContato" 
					onkeypress="return(formatarMascara(this,event,'####-####'))"/>
				</td>
		   </tr>
		   <tr>
		    	<th class="obrigatorio">Data de Inicio do Mandato:</th>
		 		<td>
	    			 <t:inputCalendar value="#{cursoLatoMBean.obj.coordenador.dataInicioMandato}" id="dataInicioCoord" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" >
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
		   </tr>
		   <tr>
		 		<th class="obrigatorio">Data de Fim do Mandato:</th>
    			<td>
	    			 <t:inputCalendar value="#{cursoLatoMBean.obj.coordenador.dataFimMandato}" id="dataFimCoord" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" >
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
		   </tr>
	    </table>
   	   </td>
	  </tr>
	  <tr>
	  	<td colspan="4">
	     <table class="subFormulario" width="100%">
		  <caption>Dados Básicos do Vice-Coordenador</caption>

			<tr>
				<th width="20%" class="obrigatorio">Vice-Coordenador:</th>
				<td>
					<h:inputText value="#{cursoLatoMBean.obj.viceCoordenador.servidor.pessoa.nome}" id="nomeViceCoord" size="59"/>
					<rich:suggestionbox for="nomeViceCoord" width="450" height="100" minChars="3" id="suggestionNomeViceCoord" 
						suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_viceCoord" 
						fetchValue="#{_viceCoord.pessoa.nome}">
					 
						<h:column>
							<h:outputText value="#{_viceCoord.pessoa.nome}" />
						</h:column>
					 
					 
					    <f:param name="apenasAtivos" value="true" />
				        <a4j:support event="onselect" actionListener="#{ cursoLatoMBean.carregaInformacao }" immediate="true">
				               <f:param name="apenasAtivos" value="true" />
				               <f:attribute name="funcaoCoordenador" value="false"/>
				               <f:attribute name="servViceCoord" value="#{_viceCoord}"/>
					    </a4j:support>
					</rich:suggestionbox>				
				</td>
			</tr>
  	       <tr>
		    	<th class="obrigatorio">Email de Contato:</th>
				<td>
					<h:inputText value="#{cursoLatoMBean.obj.viceCoordenador.emailContato}" size="50" maxlength="100" id="emailContatoVice" />
				</td>
		   </tr>

  	       <tr>
		    	<th class="obrigatorio">Telefone de Contato:</th>
				<td>
					<h:inputText value="#{cursoLatoMBean.obj.viceCoordenador.telefoneContato1}" size="10" maxlength="9" 
					id="telefoneContatoVice" onkeypress="return(formatarMascara(this,event,'####-####'))"/>
				</td>
		   </tr>
   		   <tr>
		    	<th class="obrigatorio">Data de Inicio do Mandato:</th>
		 		<td>
	    			 <t:inputCalendar value="#{cursoLatoMBean.obj.viceCoordenador.dataInicioMandato}" id="dataInicioVice" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" >
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
		   </tr>
		   <tr>
		 			<th class="obrigatorio">Data de Fim do Mandato:</th>
    			<td>
	    			 <t:inputCalendar value="#{cursoLatoMBean.obj.viceCoordenador.dataFimMandato}" id="dataFimVice" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" >
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>
		   </tr>
		   
	  	 </table>
	  	</td>
	  	</tr>
	  	<tr>
  		<td colspan="4">
    	 <table class="subFormulario" width="100%">
	  		<caption>Dados do Secretário</caption>
			<tr>
				<th width="20%" class="obrigatorio">Secretário(a):</th>
				<td>
				<h:inputText value="#{cursoLatoMBean.obj.secretario.usuario.nome}" id="usuario" style="width: 430px;" />
					<rich:suggestionbox for="usuario" height="100" width="430"  minChars="3" id="suggestion"
					   	suggestionAction="#{usuarioAutoCompleteMBean.autocompleteNomeUsuario}" var="_usuario" 
					   	fetchValue="#{_usuario.nomeLogin}">
					 
					      <h:column>
						<h:outputText value="#{_usuario.nomeLogin}" /> 
					      </h:column> 
					 
					      <a4j:support event="onselect">
						<f:setPropertyActionListener value="#{_usuario.id}" target="#{cursoLatoMBean.obj.secretario.usuario.id}"  />
					      </a4j:support>  
					</rich:suggestionbox>
				</td>
			</tr>
		 </table>	  			
  		</td>
	  	</tr>
		<tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{cursoLatoMBean.telaAnterior}" />
					<h:commandButton value="Cancelar" action="#{cursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
					<h:commandButton value="Avançar >>" action="#{cursoLatoMBean.cadastrar}" id="cadastrar" />
				</td>
		   </tr>
		</tfoot>
   </table>
</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
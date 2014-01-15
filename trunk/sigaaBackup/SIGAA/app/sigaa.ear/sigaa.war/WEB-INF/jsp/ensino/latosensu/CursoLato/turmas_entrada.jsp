<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:steps/>
</h2>

<center>

<html:form action="/ensino/latosensu/criarCurso" method="post">
	<table class="formulario">
	<caption>Dados da Turma de Entrada</caption>
	<tr>
		<th class="required">Código:</th>
		<td align="left">
			<html:text property="turmaEntrada.codigo" size="5" maxlength="5" />
		</td>
	</tr>
	<tr>
		<th class="required">Data Inicial:</th>
		<td align="left">
			<ufrn:calendar property="dataInicioTurmaEntrada" />
		</td>
	</tr>
	<tr>
		<th class="required">Data Final:</th>
		<td align="left">
			<ufrn:calendar property="dataFimTurmaEntrada" />
		</td>
	</tr>
	<tr>
		<th class="required">Vagas:</th>
		<td align="left">
			<html:text property="turmaEntrada.vagas" size="6" maxlength="3" onkeyup="formatarInteiro(this)" />
		</td>
	</tr>
 	<tr>
		<th class="required">Turno:</th>
		<td align="left">
			<html:select property="turmaEntrada.turno.id" >
          		<html:option value=""> -- SELECIONE -- </html:option>
           		<html:options collection="turnos" property="id" labelProperty="descricao"/>
       		</html:select>
		</td>
	</tr>
	<tr>
		<th class="required">Tipo Periodicidade Aula:</th>
		<td align="left">
			<html:select property="turmaEntrada.tipoPeriodicidadeAula.id" >
            	<html:option value=""> -- SELECIONE -- </html:option>
            	<html:options collection="tiposPeriodicidadeAula" property="id" labelProperty="descricao"/>
        	</html:select>
		</td>
	</tr>
 	<tr>
		<th class="required">Município:</th>
		<td align="left">
			<html:select property="turmaEntrada.municipio.id" >
            	<html:option value=""> -- SELECIONE -- </html:option>
            	<html:options collection="municipios" property="id" labelProperty="nome"/>
        	</html:select>
		</td>
	</tr>
	<tr>
		<th class="required">Campus:</th>
		<td align="left">
			<html:select property="turmaEntrada.campusIes.id" >
            	<html:option value=""> -- SELECIONE -- </html:option>
            	<html:options collection="campi" property="id" labelProperty="nome"/>
        	</html:select>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="adicionarTurmaEntrada" value="Adicionar"/>
			</td>
		</tr>
	</tfoot>
</table>


<br>
	<div class="infoAltRem">
	    <html:img page="/img/delete.gif" style="overflow: visible;"/> : Remover esta Turma de Entrada
	</div>

	    <table class="listagem" width="80%">
		<caption class="listagem">Turmas de Entrada</caption>
	        <thead>
	        <tr>
	        	<td>Código/Cidade</td>
		        <td style="text-align: center;">Data Início</td>
		        <td style="text-align: center;">Data Fim</td>
		        <td style="text-align: right;">Vagas</td>
		        <td></td>
		    </tr>
	        </thead>
	        <tbody>
			
		  <c:choose> 
		  <c:when test="${not empty cursoLatoForm.obj.turmasEntrada}">  
		    <c:forEach items="${cursoLatoForm.obj.turmasEntrada}" var="turmaEntrada">
	            <tr>
					<td>${turmaEntrada.descricao}</td>
	                <td style="text-align: center;"><fmt:formatDate value="${turmaEntrada.dataInicio}"/></td>
	                <td style="text-align: center;"><fmt:formatDate value="${turmaEntrada.dataFim}"/></td>
	                <td style="text-align: right;">${turmaEntrada.vagas}</td>
	                 <td align="right">
	                 	<html:link action="/ensino/latosensu/criarCurso?dispatch=removerTurmaEntrada&id=${turmaEntrada.id}&codigo=${turmaEntrada.codigo}&municipioId=${turmaEntrada.municipio.id}" onclick="${confirmDelete}">
                        	<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Remover esta Disciplina do curso" title="Remover" border="0"/>
	                    </html:link>
	                 </td>
	            </tr>
	        </c:forEach>
	        </c:when>
	        	<c:otherwise>
			      <tr>
	        		<td colspan="5" align="center" style="color: red;">Nenhuma turma Adicionada.</td>	        
	    		  </tr>
	        	</c:otherwise>
	      </c:choose>
	        </tbody>
				<tfoot>
					<tr align="center">
						<td colspan="5">
							<html:button dispatch="gravar" value="Gravar"/>
							<html:button view="docentes" value="<< Voltar"/>
							<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
							<html:button dispatch="disciplinas" value="Avançar >>"/>
						</td>
					</tr>
				</tfoot>
	    </table>


<br>
</html:form>
</center>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
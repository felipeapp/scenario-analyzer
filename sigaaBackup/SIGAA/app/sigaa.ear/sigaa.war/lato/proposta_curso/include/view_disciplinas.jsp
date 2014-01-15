		  	<caption>Disciplinas do Curso</caption>
				<tr>
					<td><b>Código</b></td>
					<td><b>Nome</b></td>
					<td style="text-align: right;"><b>Carga Horária</b></td>
				</tr>
				<c:choose> 
				 <c:when test="${not empty cursoLatoMBean.allDisciplinasCursoLato}">  
			       <c:forEach items="#{cursoLatoMBean.allDisciplinasCursoLato}" var="ccl" varStatus="status">
			            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			            	<td>${ccl.codigo}</td>
 			                <td>${ccl.nomeCurso}</td>
			                <td style="text-align: right;">${ccl.cargaHorariaTotal} h</td>
			            </tr>
			            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			            	<td colspan="4"><b>Ementa:</b><br />${ccl.ementa}</td>
			            </tr>
			            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	            	
			            	<td colspan="4"><b>Bibliografia:</b><br />${ccl.bibliografia}</td>
			            </tr>
			            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	            	
			            	<td colspan="4"><b>Docente(s):</b></td>
			            </tr>
	            		<c:forEach items="#{ccl.nomeDocente}" var="nomes">
	          				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
	         					<td colspan="2">${nomes.key}</td>
	          					<td style="text-align: right;">${nomes.value} h</td>
	          				</tr>
	          			</c:forEach>
			       </c:forEach>
				  </c:when>
			       <c:otherwise>
						<tr>
							<td colspan="5" align="center" style="color: red;">Nenhuma disciplina adicionada.</td>
						</tr>
				    </c:otherwise>
				</c:choose>